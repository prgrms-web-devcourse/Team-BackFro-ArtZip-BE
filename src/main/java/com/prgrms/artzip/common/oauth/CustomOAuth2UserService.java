package com.prgrms.artzip.common.oauth;

import static com.prgrms.artzip.common.ErrorCode.ROLE_NOT_FOUND;
import static org.springframework.util.StringUtils.*;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.oauth.dto.OAuth2UserInfo;
import com.prgrms.artzip.common.oauth.dto.OAuth2UserInfoFactory;
import com.prgrms.artzip.user.domain.OAuthUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.repository.RoleRepository;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
    try {
      return processOAuth2User(oAuth2UserRequest, oAuth2User);
    } catch (AuthErrorException ex) {
      throw ex;
    } catch (Exception ex) {
      // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
    if(!hasText(oAuth2UserInfo.getEmail())) {
      throw new AuthErrorException(ErrorCode.OAUTH_EMAIL_REQUIRED);
    }
    AuthProvider provider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase());
    OAuthUser oAuthUser = userRepository.findByProviderAndProviderId(provider, oAuth2User.getName())
        .orElseGet(() -> oauthSignUp(oAuth2UserRequest, oAuth2UserInfo));
    return new OAuthUserPrincipal(oAuthUser, oAuth2User.getAttributes());
  }

  private OAuthUser oauthSignUp(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
    Role userRole = roleRepository.findByAuthority(Authority.USER)
        .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
    OAuthUser oAuthUser = OAuthUser.builder()
        .email(oAuth2UserInfo.getEmail())
        .nickname(oAuth2UserInfo.getNickName())
        .provider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toLowerCase()))
        .providerId(oAuth2UserInfo.getId())
        .roles(List.of(userRole))
        .build();
    if(hasText(oAuth2UserInfo.getImageUrl())) oAuthUser.setProfileImage(oAuth2UserInfo.getImageUrl());
    return userRepository.save(oAuthUser);
  }

}
