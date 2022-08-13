package com.prgrms.artzip.user.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class SimpleUserResponse {

  private final Long userId;
  private final String profileImage;
  private final String nickname;
  private final String email;

}
