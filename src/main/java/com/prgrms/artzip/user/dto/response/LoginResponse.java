package com.prgrms.artzip.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

  private final Long userId;

  private final String accessToken;

  private final String refreshToken;
}
