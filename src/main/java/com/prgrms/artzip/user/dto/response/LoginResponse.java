package com.prgrms.artzip.user.dto.response;

import lombok.Getter;

@Getter
public class LoginResponse {

  private final Long userId;
  private final String accessToken;

  private final String refreshToken;

  public LoginResponse(Long userId, String accessToken, String refreshToken) {
    this.userId = userId;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
