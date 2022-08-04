package com.prgrms.artzip.user.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class UserLocalLoginRequest {
  @NotBlank
  private final String email;

  @NotBlank
  private final String password;
}
