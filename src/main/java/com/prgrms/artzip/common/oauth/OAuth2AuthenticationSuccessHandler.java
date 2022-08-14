package com.prgrms.artzip.common.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.artzip.common.util.CookieUtil;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.dto.response.LoginResponse;
import com.prgrms.artzip.user.service.UserService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {
  private final Logger log = LoggerFactory.getLogger(getClass());

  private final JwtService jwtService;

  private final UserService userService;

  private final ObjectMapper objectMapper;
  private static final String REFRESH_TOKEN = "refreshToken";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
    if (authentication instanceof OAuth2AuthenticationToken oauth2Token) {
      OAuth2User principal = oauth2Token.getPrincipal();
      String registrationId = oauth2Token.getAuthorizedClientRegistrationId();
      User user = processUserOAuth2UserJoin(principal, registrationId);

      response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);

      String targetUri = determineTargetUrl(request, response, user);
      getRedirectStrategy().sendRedirect(request, response, targetUri);
    } else {
      super.onAuthenticationSuccess(request, response, authentication);
    }
  }

  private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, User user) {

    String targetUri = "http://localhost:3000/oauth/callback";
    String accessToken = generateAccessToken(user);
    String refreshToken = generateRefreshToken(user);

    return UriComponentsBuilder.fromUriString(targetUri)
        .queryParam("refreshToken", refreshToken)
        .queryParam("accessToken", accessToken)
        .queryParam("userId", user.getId())
        .build().toUriString();
  }

  private User processUserOAuth2UserJoin(OAuth2User oAuth2User, String registrationId) {
    return userService.oauthSignUp(oAuth2User, registrationId);
  }

  private String generateAccessToken(User user) {
    return jwtService.createAccessToken(user.getId(), user.getEmail(), user.getRoles().stream().map(
        Role::toGrantedAuthority).collect(
        Collectors.toList()));
  }

  private String generateRefreshToken(User user) {
    return jwtService.createRefreshToken(user.getEmail());
  }
}
