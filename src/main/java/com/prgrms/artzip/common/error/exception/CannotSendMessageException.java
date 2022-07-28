package com.prgrms.artzip.common.error.exception;

import com.prgrms.artzip.common.ErrorCode;
import lombok.Getter;

@Getter
public class CannotSendMessageException extends RuntimeException {

  private final ErrorCode errorCode;

  public CannotSendMessageException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
