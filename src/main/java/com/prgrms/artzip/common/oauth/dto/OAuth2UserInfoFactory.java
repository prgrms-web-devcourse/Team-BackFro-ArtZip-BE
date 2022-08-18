package com.prgrms.artzip.common.oauth.dto;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.oauth.AuthProvider;
import java.util.Map;

public class OAuth2UserInfoFactory {

  public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
    if (registrationId.equalsIgnoreCase(AuthProvider.kakao.toString())) {
      return new KakaoOAuth2UserInfo(attributes);
    } else {
      throw new AuthErrorException(ErrorCode.OAUTH_PROVIDER_UNSUPPORTED);
    }
  }
}
