package com.prgrms.artzip.common.error;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.ErrorResponse;
import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.AuthErrorException;
import com.prgrms.artzip.common.error.exception.CannotSendMessageException;
import com.prgrms.artzip.common.error.exception.DuplicateRequestException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.error.exception.PermissionDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  // 500 : Internal Server Error
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleServerException(Exception e) {
    return handleException(e, ErrorCode.INTERNAL_SERVER_ERROR);
  }

  // 405 : Method Not Allowed
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e) {
    return handleException(e, ErrorCode.METHOD_NOT_ALLOWED);
  }

  // 400 : MethodArgumentNotValidException
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    return handleException(e, ErrorCode.INVALID_TYPE_VALUE);
  }

  // 400 : MethodArgumentType
  @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e) {
    return handleException(e, ErrorCode.INVALID_TYPE_VALUE);
  }

  // 400 : Bad Request, ModelAttribute
  @ExceptionHandler(org.springframework.validation.BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
    return handleException(e, ErrorCode.INVALID_INPUT_VALUE);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
      HttpMessageNotReadableException e) {
    return handleException(e, ErrorCode.INVALID_INPUT_VALUE);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    return handleException(e, ErrorCode.INVALID_INPUT_VALUE);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFoundException(NoHandlerFoundException e) {
    return handleException(e, ErrorCode.NOT_FOUND);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
    return handleException(e, ErrorCode.MISSING_REQUEST_PARAMETER);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
    return handleException(e, ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED);
  }

  /**
   * TODO - 400: Custom Index
   */
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
    return handleException(e, e.getErrorCode());
  }

  @ExceptionHandler(AlreadyExistsException.class)
  public ResponseEntity<ErrorResponse> handleFriendExistsException(AlreadyExistsException e) {
    return handleException(e, e.getErrorCode());
  }

  @ExceptionHandler(DuplicateRequestException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateFriendRequestException(
      DuplicateRequestException e) {
    return handleException(e, e.getErrorCode());
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleInvalidRequestException(InvalidRequestException e) {
    return handleException(e, e.getErrorCode());
  }

  @ExceptionHandler(AuthErrorException.class)
  public ResponseEntity<ErrorResponse> handleAuthErrorException(AuthErrorException e) {
    return handleException(e, e.getErrorCode());
  }

  @ExceptionHandler(PermissionDeniedException.class)
  public ResponseEntity<ErrorResponse> handlePermissionDeniedException(
      PermissionDeniedException e) {
    return handleException(e, e.getErrorCode());
  }

  @ExceptionHandler(CannotSendMessageException.class)
  public ResponseEntity<ErrorResponse> handleCannotSendMessageException(CannotSendMessageException e) {
    return handleException(e, e.getErrorCode());
  }

  private ResponseEntity<ErrorResponse> handleException(Exception e, ErrorCode errorCode) {
    log.warn(e.getMessage(), e);
    ErrorResponse errorResponse = ErrorResponse.of(errorCode);
    return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
  }
}
