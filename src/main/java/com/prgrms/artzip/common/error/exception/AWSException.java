package com.prgrms.artzip.common.error.exception;

import com.prgrms.artzip.common.ErrorCode;
import lombok.Getter;

@Getter
public class AWSException extends RuntimeException{

  private final ErrorCode errorCode;

  public AWSException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
