package com.prgrms.artzip.user.dto.request;

import lombok.*;

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
