package com.prgrms.artzip.user.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordUpdateRequest {

  @NotBlank
  private String oldPassword;

  @NotBlank
  private String newPassword;
}
