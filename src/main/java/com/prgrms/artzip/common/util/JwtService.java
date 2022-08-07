package com.prgrms.artzip.common.util;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.prgrms.artzip.common.config.JwtConfig;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.jwt.Jwt;
import com.prgrms.artzip.common.jwt.claims.AccessClaim;
import com.prgrms.artzip.common.jwt.claims.RefreshClaim;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import static com.prgrms.artzip.common.ErrorCode.*;

@Component
public class JwtService {

  private final Jwt accessJwt;

  private final Jwt refreshJwt;

  private final JwtConfig jwtConfig;

  private final RedisService redisService;

  public JwtService(@Qualifier("accessJwt") Jwt accessJwt, @Qualifier("refreshJwt") Jwt refreshJwt, JwtConfig jwtConfig,
                    RedisService redisService) {
    this.accessJwt = accessJwt;
    this.refreshJwt = refreshJwt;
    this.jwtConfig = jwtConfig;
    this.redisService = redisService;
  }

  public String createAccessToken(Long userId, String email, List<GrantedAuthority> authorities) {
    String[] roles = authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .toArray(String[]::new);
    return accessJwt.sign(new AccessClaim(userId, email, roles));
  }

  public String createRefreshToken(String email) {
    String refreshToken = refreshJwt.sign(new RefreshClaim(email));
    redisService.setValues(email, refreshToken, Duration.ofMillis(
        jwtConfig.getRefreshToken().getExpirySeconds()));
    return refreshToken;
  }

  public void checkRefreshToken(String email, String refreshToken) {
    try{
      refreshJwt.verifyRefreshToken(refreshToken);
    } catch (TokenExpiredException e) {
      throw new AuthErrorException(TOKEN_EXPIRED);
    }
    String redisToken = redisService.getValues(email);
    if(!redisToken.equals(refreshToken)) {
      throw new AuthErrorException(INVALID_REFRESH_TOKEN_REQUEST);
    }
  }

  public void logout(String token) {
    AccessClaim claim = accessJwt.verifyAccessToken(token);
    long expiredAccessTokenTime = claim.getExp().getTime() - new Date().getTime();
    redisService.setValues(jwtConfig.getBlackListPrefix() + token, claim.getEmail(), Duration.ofMillis(expiredAccessTokenTime));
    redisService.deleteValues(claim.getEmail());
  }

  public AccessClaim verifyAccessToken(String token) {
    String expiredAt = redisService.getValues(jwtConfig.getBlackListPrefix() + token);
    if (expiredAt != null) throw new AuthErrorException(BLACKLIST_TOKEN_REQUEST);
    return accessJwt.verifyAccessToken(token);
  }
}
