package com.prgrms.artzip.common.util;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.prgrms.artzip.common.config.JwtConfig;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.jwt.Jwt;
import com.prgrms.artzip.common.jwt.claims.AccessClaim;
import com.prgrms.artzip.common.jwt.claims.RefreshClaim;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static com.prgrms.artzip.common.ErrorCode.*;

@Component
public class JwtService {

  private final Jwt jwt;

  private final JwtConfig jwtConfig;

  private final RedisService redisService;

  public JwtService(Jwt jwt, JwtConfig jwtConfig,
                    RedisService redisService) {
    this.jwt = jwt;
    this.jwtConfig = jwtConfig;
    this.redisService = redisService;
  }

  public String createAccessToken(Long userId, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    return jwt.sign(new AccessClaim(userId, email, roles));
  }

  public String createRefreshToken(String email) {
    String refreshToken = jwt.sign(new RefreshClaim(email));
    redisService.setValues(email, refreshToken, Duration.ofMillis(
        jwtConfig.getRefreshToken().getExpirySeconds()));
    return refreshToken;
  }

  public void checkRefreshToken(String email, String refreshToken) {
    try{
      jwt.verifyRefreshToken(refreshToken);
    } catch (TokenExpiredException e) {
      throw new AuthErrorException(TOKEN_EXPIRED);
    }
    String redisToken = redisService.getValues(email);
    if(!redisToken.equals(refreshToken)) {
      throw new AuthErrorException(INVALID_TOKEN_REQUEST);
    }
  }

  public void logout(String token) {
    AccessClaim claim = jwt.verifyAccessToken(token);
    long expiredAccessTokenTime = claim.getExp().getTime() - new Date().getTime();
    redisService.setValues(jwtConfig.getBlackListPrefix() + token, claim.getEmail(), Duration.ofMillis(expiredAccessTokenTime));
    redisService.deleteValues(claim.getEmail());
  }

  public AccessClaim verifyAccessToken(String token) {
    String expiredAt = redisService.getValues(jwtConfig.getBlackListPrefix() + token);
    if (expiredAt != null) throw new AuthErrorException(TOKEN_EXPIRED);
    return jwt.verifyAccessToken(token);
  }
}
