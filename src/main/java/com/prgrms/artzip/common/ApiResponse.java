package com.prgrms.artzip.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(Include.NON_NULL)
public class ApiResponse<T> {
  private String message;
  private Integer code;
  private T data;

  public ApiResponse(String message, Integer code) {
    this.message = message;
    this.code = code;
  }

  public ApiResponse(String message, Integer code, T data) {
    this.message = message;
    this.code = code;
    this.data = data;
  }
}
