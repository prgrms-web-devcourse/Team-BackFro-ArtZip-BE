package com.prgrms.artzip.common.oauth.dto;

import java.util.Map;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo{

  private Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
  private Map<String, Object> accountInfo = (Map<String, Object>) attributes.get("kakao_account");
  public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return (String) attributes.get("id");
  }

  @Override
  public String getNickName() {
    return (String) properties.get("nickname");
  }

  @Override
  public String getEmail() {
    return (String) accountInfo.get("email");
  }

  @Override
  public String getImageUrl() {
    return (String) properties.get("profile_image");
  }
}
