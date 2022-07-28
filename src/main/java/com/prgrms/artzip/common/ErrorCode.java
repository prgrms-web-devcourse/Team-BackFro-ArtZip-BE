package com.prgrms.artzip.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum ErrorCode {
  // Internal Server Error
  INTERNAL_SERVER_ERROR(500, "server01", "서버에 문제가 생겼습니다."),

  // 400 Client Error
  METHOD_NOT_ALLOWED(405, "C001", "적절하지 않은 HTTP 메소드입니다."),
  INVALID_TYPE_VALUE(400, "C002", "요청 값의 타입이 잘못되었습니다."),
  INVALID_INPUT_VALUE(400, "C003", "적절하지 않은 값입니다."),
  NOT_FOUND(404, "C004", "해당 리소스를 찾을 수 없습니다."),
  BAD_REQUEST(400, "C005", "잘못된 요청입니다."),
  MISSING_REQUEST_PARAMETER(400, "C005", "필수 파라미터가 누락되었습니다."),
  INVALID_LENGTH(400, "C006", "올바르지 않은 길이입니다."),

  /**
   * User Domain
   */
  USER_NOT_FOUND(400, "U001", "유저가 존재하지 않습니다."),
  INVALID_ACCOUNT_REQUEST(400, "U002", "아이디 및 비밀번호가 올바르지 않습니다."),
  INVALID_TOKEN_REQUEST(400, "U003", "토큰이 올바르지 않습니다."),
  USER_ALREADY_EXISTS(400, "U004", "유저가 이미 존재합니다."),
  TOKEN_EXPIRED(400, "U005", "토큰이 만료되었습니다.");


  private final int status;
  private final String code;
  private final String message;

  ErrorCode(int status, String code, String message) {
    this.status = status;
    this.code = code;
    this.message = message;
  }

  private static final Map<String, ErrorCode> messageMap
      = Collections.unmodifiableMap(Stream.of(values())
      .collect(Collectors.toMap(ErrorCode::getMessage, Function.identity())));

  public static ErrorCode fromMessage(String message) {
    return messageMap.get(message);
  }
}
