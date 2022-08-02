package com.prgrms.artzip.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
  private String message;
  private Integer status;
  private T data;

  @Builder
  public ApiResponse(String message, Integer status, T data) {
    this.message = message;
    this.status = status;
    this.data = data;
  }
}
