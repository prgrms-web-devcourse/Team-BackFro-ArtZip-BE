package com.prgrms.artzip.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequest {

  @NotBlank
  private String email;

  @NotBlank
  private String nickname;

  @NotBlank
  private String password;
}
