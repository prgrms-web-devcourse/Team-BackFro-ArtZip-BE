package com.prgrms.artzip.user.dto.response;

import lombok.Getter;

@Getter
public class UniqueCheckResponse {

  private final Boolean isUnique;

  public UniqueCheckResponse(Boolean isUnique) {
    this.isUnique = isUnique;
  }
}
