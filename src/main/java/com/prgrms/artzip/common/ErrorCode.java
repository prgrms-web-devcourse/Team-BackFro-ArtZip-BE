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
  TOKEN_EXPIRED(400, "U005", "토큰이 만료되었습니다."),

  /**
   * Exhibition Domain
   */
  INVALID_EXHBN_SEQ(400, "EX001", "전시회 seq는 필수입니다."),
  INVALID_EXHBN_NAME(400, "EX002", "전시회 이름은 필수입니다.(1 <= 전시회 이름 <= 70)"),
  INVALID_EXHBN_PERIOD(400, "EX003", "전시회 기간 정보는 필수입니다.(startDate <= endDate)"),
  INVALID_EXHBN_DESCRIPTION(400, "EX004", "전시회 설명은 최대 1000자 입니다."),
  INVALID_EXHBN_COORDINATE(400, "EX005", "전시회 좌표는 필수입니다."),
  INVALID_EXHB_AREA(400, "EX006", "전시회 지역은 필수입니다."),
  INVALID_EXHB_PLACE(400, "EX007", "전시회 장소는 필수입니다.(1 <= 전시회 장소 <= 20)"),
  INVALID_EXHB_ADDRESS(400, "EX008", "전시회 상세 주소는 필수입니다.(1 <= 전시회 주소 <= 100)"),
  INVALID_EXHB_INQUIRY(400, "EX008", "전시회 문의처는 필수입니다.(1 <= 전시회 문의처 <= 100)"),
  INVALID_EXHB_FEE(400, "EX009", "전시회 요금 정보는 필수입니다.(1 <= 전시회 요금 정보 <= 1000)"),
  INVALID_EXHB_THUMBNAIL(400, "EX010",
      "전시회 썸네일을 필수입니다. 전시회 썸네일 저장 형태는 URL 입니다.(1 <= 전시회 썸네일 URL <= 2083)"),
  INVALID_EXHB_URL(400, "EX011", "전시회 URL은 필수입니다.(1 <= 전시회 URL <= 2083)"),
  INVALID_EXHB_PLACEURL(400, "EX012", "전시회 장소 URL은 필수입니다.(1 <= 전시회 장소 URL <= 2083)"),
  INVALID_EXHB_LIKE(400, "EX013", "전시회 좋아요에는 전시회 정보와 사용자 정보가 필수입니다."),
  EXHB_NOT_FOUND(404, "EX014", "존재하지 않는 전시회 입니다."),
  EXHB_QUERY_BLANK(400, "EX015", "검색어(query)는 필수 입니다."),

  /**
   * Comment Domain
   */
  COMMENT_NOT_FOUND(400, "C001", "댓글이 존재하지 않습니다."),
  CONTENT_IS_REQUIRED(400, "C002", "댓글 내용은 필수입니다.(최대 500자)"),
  CONTENT_IS_TOO_LONG(400, "C003", "댓글은 최대 500자입니다."),

  /**
   * Review Domain
   */
  REVIEW_FIELD_CONTAINS_NULL_VALUE(400, "R001", "리뷰 필드에 NULL값이 포함되어 있습니다."),
  INVALID_REVIEW_CONTENT_LENGTH(400, "R002", "리뷰 내용은 1글자 이상 1000자 이하이어야 합니다."),
  INVALID_REVIEW_TITLE_LENGTH(400, "R003", "리뷰 제목은 1글자 이상 50자 이하이어야 합니다."),
  INVALID_REVIEW_DATE(400, "R004", "방문일은 오늘 이후일 수 없습니다."),
  REVIEW_LIKE_FIELD_CONTAINS_NULL_VALUE(400, "R005", "리뷰 좋아요 필드에 NULL값이 포함되어 있습니다."),
  REVIEW_PHOTO_FIELD_CONTAINS_NULL_VALUE(400, "R006", "리뷰 사진 필드에 NULL값이 포함되어 있습니다."),
  INVALID_REVIEW_PHOTO_PATH_LENGTH(400, "R007", "path는 1글자 이상 2083자 이하이어야 합니다.");


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
