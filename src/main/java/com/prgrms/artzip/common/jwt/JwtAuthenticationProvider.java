package com.prgrms.artzip.common.jwt;

import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.service.UserService;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationProvider implements AuthenticationProvider {

  private final JwtService jwtService;

  private final UserService userService;

  public JwtAuthenticationProvider(
      JwtService jwtService, UserService userService) {
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication;
    return processUserAuthentication(String.valueOf(jwtAuthentication.getPrincipal()), jwtAuthentication.getCredentials());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    Assert.isAssignable(authentication, JwtAuthenticationToken.class);
    return true;
  }

  private Authentication processUserAuthentication(String principal, String credentials) {
    try{
      User user = userService.login(principal, credentials);
      List<GrantedAuthority> authorities = user.getRoles().stream()
              .map(role -> new SimpleGrantedAuthority(role.getAuthority().name())).collect(Collectors.toList());
      String accessToken = jwtService.createAccessToken(user.getId(), user.getEmail(), authorities);
      String refreshToken = jwtService.createRefreshToken(user.getEmail());
      JwtAuthenticationToken authenticated = new JwtAuthenticationToken(new JwtPrincipal(accessToken, user.getEmail(), user.getId()), null, authorities);
      authenticated.setDetails(refreshToken);
      return authenticated;
    } catch (IllegalArgumentException e) {
      throw new BadCredentialsException(e.getMessage());
    } catch (DataAccessException e) {
      throw new AuthenticationServiceException(e.getMessage(), e);
    }
  }
}
