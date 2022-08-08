package com.prgrms.artzip.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLocalLoginRequest {

  @NotBlank
  private String email;
  @NotBlank
  private String password;
}
