package com.prgrms.artzip.common.error.exception;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.coretime.common.ErrorCode;
import lombok.Getter;

@Getter
public class AlreadyExistsException extends RuntimeException{

  private final ErrorCode errorCode;

  public AlreadyExistsException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
