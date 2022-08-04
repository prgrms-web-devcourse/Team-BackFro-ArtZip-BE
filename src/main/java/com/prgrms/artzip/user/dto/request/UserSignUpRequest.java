package com.prgrms.artzip.user.dto.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
public class UserSignUpRequest {

  @NotBlank
  private final String email;

  @NotBlank
  private final String nickname;

  @NotBlank
  private final String password;
}
