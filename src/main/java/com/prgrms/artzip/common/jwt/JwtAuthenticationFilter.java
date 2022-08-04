package com.prgrms.artzip.common.jwt;

import static java.util.Objects.*;
import static org.springframework.util.StringUtils.*;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.prgrms.artzip.common.jwt.claims.AccessClaim;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.service.UserUtilService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final String accessHeaderKey;

  private final JwtService jwtService;

  private final UserUtilService userUtilService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String token = getAccessToken(request);
      if (nonNull(token)) {
        try {
          AccessClaim claims = jwtService.verifyAccessToken(token);
          Long userId = claims.getUserId();
          List<GrantedAuthority> authorities = getAuthorities(claims);
          User currentUser = userUtilService.getUserById(userId);
          if (!isNull(userId) && authorities.size() > 0) {
            JwtAuthenticationToken authentication = new JwtAuthenticationToken(new JwtPrincipal(token, currentUser), null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
          }
        } catch (TokenExpiredException e) {
          log.warn("토큰이 만료된 요청입니다. token: {}", token);
          throw e;
        } catch (Exception e) {
          log.warn("Jwt 처리 실패: {}, class: {}", e.getMessage(), e.getClass());
          throw e;
        }
      }
    } else {
      log.debug("SecurityContextHolder는 이미 authentication 객체를 가지고 있습니다.: '{}'", SecurityContextHolder.getContext().getAuthentication());
    }

    chain.doFilter(request, response);
  }

  private String getAccessToken(HttpServletRequest request) {
    String token = request.getHeader(accessHeaderKey);
    if(hasText(token)) {
      log.debug("Jwt authorization api detected: {}", token);
      return URLDecoder.decode(token, StandardCharsets.UTF_8);
    }
    return null;
  }

  private List<GrantedAuthority> getAuthorities(AccessClaim claims) {
    String[] roles = claims.getRoles();
    return roles == null || roles.length == 0 ? Collections.emptyList() : Arrays.stream(roles).map(
        SimpleGrantedAuthority::new).collect(Collectors.toList());
  }
}
