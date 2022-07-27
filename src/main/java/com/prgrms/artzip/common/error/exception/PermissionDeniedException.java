package com.prgrms.artzip.common.error.exception;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.coretime.common.ErrorCode;
import lombok.Getter;

@Getter
public class PermissionDeniedException extends RuntimeException{

  private final ErrorCode errorCode;

  public PermissionDeniedException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
