package com.prgrms.artzip.user.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenReissueRequest {

  @NotNull
  private Long userId;

  @NotBlank
  private String accessToken;

  @NotBlank
  private String refreshToken;

}
