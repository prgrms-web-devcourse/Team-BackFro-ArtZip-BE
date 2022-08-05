package com.prgrms.artzip.user.dto.response;

import com.prgrms.artzip.user.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignUpResponse {

  private final Long userId;

  private final String email;

  private final String nickname;

  public static SignUpResponse from(User user) {
    return SignUpResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .build();
  }
}
