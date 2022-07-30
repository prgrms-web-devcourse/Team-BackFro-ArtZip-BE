package com.prgrms.artzip.user.dto.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class UserLocalLoginRequest {
  @NotBlank
  private final String email;

  @NotBlank
  private final String password;

  public UserLocalLoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
