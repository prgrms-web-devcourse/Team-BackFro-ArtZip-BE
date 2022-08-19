package com.prgrms.artzip.common.oauth;

import static com.prgrms.artzip.common.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import com.prgrms.artzip.common.util.CookieUtil;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final JwtService jwtService;

  private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
  private static final String FRONT_PROD_URL = "https://artzip.shop/oauth/callback";

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws ServletException, IOException {
    if (authentication.getPrincipal() instanceof OAuthUserPrincipal principal) {
      String targetUrl = determineTargetUrl(request, response, principal.getOAuthUser());
      if (response.isCommitted()) {
        log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
        return;
      }
      clearAuthenticationAttributes(request, response);
      getRedirectStrategy().sendRedirect(request, response, targetUrl);
    } else {
      super.onAuthenticationSuccess(request, response, authentication);
    }
  }


  private String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
      User user) {

    Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
        .map(Cookie::getValue);

    // TODO: 프론트에서 보낸 redirect url이 올바른 파라미터인가 validation

    String targetUri = redirectUri.orElse(FRONT_PROD_URL);
    String accessToken = generateAccessToken(user);
    String refreshToken = generateRefreshToken(user);

    return UriComponentsBuilder.fromUriString(targetUri)
        .queryParam("refreshToken", refreshToken)
        .queryParam("accessToken", accessToken)
        .queryParam("userId", user.getId())
        .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request,
      HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request,
        response);
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
