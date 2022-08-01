package com.prgrms.artzip.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
  private String message;
  private Integer code;
  private T data;

  @Builder
  public ApiResponse(String message, Integer code, T data) {
    this.message = message;
    this.code = code;
    this.data = data;
  }
}
