package com.prgrms.artzip.user.dto.response;

import com.prgrms.artzip.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegisterResponse {
  private final Long userId;

  private final String email;

  private final String nickname;

  public static RegisterResponse from(User user) {
    return RegisterResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .build();
  }
}
