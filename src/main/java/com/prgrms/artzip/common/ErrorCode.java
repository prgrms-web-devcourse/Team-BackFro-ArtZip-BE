package com.prgrms.artzip.common;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum ErrorCode {
  // Server Error
  INTERNAL_SERVER_ERROR(500, "S000", "서버에 문제가 생겼습니다."),
  AMAZON_S3_ERROR(500, "S001", "AWS S3와의 연동에 문제가 생겼습니다."),

  // Client Error
  METHOD_NOT_ALLOWED(405, "C000", "적절하지 않은 HTTP 메소드입니다."),
  INVALID_TYPE_VALUE(400, "C001", "요청 값의 타입이 잘못되었습니다."),
  INVALID_INPUT_VALUE(400, "C002", "적절하지 않은 값입니다."),
  NOT_FOUND(404, "C003", "해당 리소스를 찾을 수 없습니다."),
  BAD_REQUEST(400, "C004", "잘못된 요청입니다."),
  MISSING_REQUEST_PARAMETER(400, "C005", "필수 파라미터가 누락되었습니다."),
  INVALID_LENGTH(400, "C006", "올바르지 않은 길이입니다."),
  INVALID_FILE_EXTENSION(400, "C007", "올바르지 않은 파일 확장자입니다. (png, jpg, jpeg 가능)"),
  MAX_UPLOAD_SIZE_EXCEEDED(400, "C008", "최대 파일 크기(5MB)보다 큰 파일입니다."),
  RESOURCE_PERMISSION_DENIED(400, "C009", "해당 리소스에 대한 작업 권한이 없습니다."),
  ACCESS_DENIED(403, "C010", "요청 권한이 없습니다."),
  UNAUTHENTICATED_USER(401, "C011", "인증되지 않은 사용자입니다."),

  /**
   * User Domain
   */
  USER_NOT_FOUND(400, "U001", "유저가 존재하지 않습니다."),
  INVALID_ACCOUNT_REQUEST(400, "U002", "아이디 및 비밀번호가 올바르지 않습니다."),
  INVALID_REFRESH_TOKEN_REQUEST(400, "U003", "토큰이 올바르지 않습니다."),
  USER_ALREADY_EXISTS(400, "U004", "유저가 이미 존재합니다."),
  TOKEN_EXPIRED(400, "U005", "토큰이 만료되었습니다."),
  LOGIN_PARAM_REQUIRED(400, "U006", "로그인 파라미터가 누락되었습니다."),
  ACCESS_TOKEN_REQUIRED(400, "U007", "access token은 필수입니다."),
  EMAIL_REQUIRED(400, "U008", "이메일은 필수입니다."),
  ROLE_NOT_FOUND(400, "U009", "역할이 존재하지 않습니다."),
  USER_PARAM_REQUIRED(400, "U010", "유저가 누락되었습니다."),
  USER_PROFILE_NOT_MATCHED(400, "U011", "수정할 프로필 사진이 누락되었으며, 유저의 기존 프로필 이미지와 다른 링크입니다."),
  NICKNAME_ALREADY_EXISTS(400, "U012", "이미 존재하는 닉네임입니다."),
  TOKEN_NOT_EXPIRED(400, "U013", "토큰이 아직 만료되지 않았으므로 재발행할 수 없습니다."),
  PASSWORD_CANNOT_BE_SAME(400, "U014", "새 비밀번호는 이전 비밀번호와 같을 수 없습니다."),
  REDIS_TOKEN_NOT_FOUND(500, "U015", "유저에 해당하는 토큰을 찾을 수 없습니다."),
  TOKEN_USER_ID_NOT_MATCHED(400, "U016", "액세스 토큰과 유저 아이디가 매치되지 않습니다."),
  BLACKLIST_TOKEN_REQUEST(400, "U017", "로그아웃 처리된 토큰으로 요청할 수 없습니다."),
  OAUTH_PROVIDER_UNSUPPORTED(500, "U018", "아직 지원되지 않은 소셜로그인입니다."),

  OAUTH_EMAIL_REQUIRED(500, "U019", "OAuth email을 수집하는데 실패하였습니다."),
  /**
   * Exhibition Domain
   */
  INVALID_EXHBN_NAME(400, "EX002", "전시회 이름은 필수입니다.(1 <= 전시회 이름 <= 70)"),
  INVALID_EXHBN_PERIOD(400, "EX003", "전시회 기간 정보는 필수입니다.(startDate <= endDate)"),
  INVALID_EXHBN_DESCRIPTION(400, "EX004", "전시회 설명은 최대 5000자 입니다."),
  INVALID_EXHBN_COORDINATE(400, "EX005", "전시회 좌표는 필수입니다.(-90 <= 위도 <= 90, -180 <= 경도 <= 180)"),
  INVALID_EXHB_AREA(400, "EX006", "전시회 지역은 필수입니다."),
  INVALID_EXHB_PLACE(400, "EX007", "전시회 장소는 필수입니다.(1 <= 전시회 장소 <= 20)"),
  INVALID_EXHB_ADDRESS(400, "EX008", "전시회 상세 주소는 필수입니다.(1 <= 전시회 주소 <= 100)"),
  INVALID_EXHB_INQUIRY(400, "EX009", "전시회 문의처는 필수입니다.(1 <= 전시회 문의처 <= 100)"),
  INVALID_EXHB_FEE(400, "EX010", "전시회 요금 정보는 필수입니다.(1 <= 전시회 요금 정보 <= 1000)"),
  INVALID_EXHB_THUMBNAIL(400, "EX011",
      "전시회 썸네일을 필수입니다. 전시회 썸네일 저장 형태는 URL 입니다.(1 <= 전시회 썸네일 URL <= 2083)"),
  INVALID_EXHB_URL(400, "EX012", "전시회 URL은 필수입니다.(1 <= 전시회 URL <= 2083)"),
  INVALID_EXHB_PLACEURL(400, "EX013", "전시회 장소 URL은 필수입니다.(1 <= 전시회 장소 URL <= 2083)"),
  INVALID_EXHB_LIKE(400, "EX014", "전시회 좋아요에는 전시회 정보와 사용자 정보가 필수입니다."),
  EXHB_NOT_FOUND(400, "EX015", "존재하지 않는 전시회 입니다."),
  INVALID_EXHB_QUERY(400, "EX016", "검색어는 필수입니다.(2 <= 검색어)"),
  INVALID_EXHB_QUERY_FOR_REVIEW(400, "EX017", "검색어는 필수입니다."),
  INVALID_CUSTOM_EXHB_CONDITION(400, "EX018", "areas, months, genres 에는 null이 포함될 수 없습니다"),
  INVALID_COORDINATE(400, "EX019", "옳지 않은 위도 경도 정보입니다.(-90 <= 위도 <= 90, -180 <= 경도 <= 180)"),
  INVALID_DISTANCE(400, "EX020", "거리는 0 이상이어야 합니다."),
  INVALID_EXHB_SORT_TYPE(400, "EX021", "유효하지 않은 전시회 정렬 조건입니다."),

  /**
   * Comment Domain
   */
  COMMENT_NOT_FOUND(400, "CM001", "댓글이 존재하지 않습니다."),
  CONTENT_IS_REQUIRED(400, "CM002", "댓글 내용은 필수입니다.(최대 500자)"),
  CONTENT_IS_TOO_LONG(400, "CM003", "댓글은 최대 500자입니다."),
  COMMENT_USER_IS_REQUIRED(400, "CM004", "댓글 작성 유저는 필수입니다."),
  COMMENT_ALREADY_DELETED(400, "CM005", "댓글이 이미 삭제되었습니다."),
  CHILD_CANT_BE_PARENT(400, "CM006", "자식 댓글은 부모 댓글이 될 수 없습니다."),
  INVALID_COMMENT_SORT_TYPE(400, "CM007", "잘못된 댓글 정렬 조건입니다."),
  /**
   * Review Domain
   */
  REVIEW_FIELD_CONTAINS_NULL_VALUE(400, "R001", "리뷰 필드에 NULL값이 포함되어 있습니다."),
  INVALID_REVIEW_CONTENT_LENGTH(400, "R002", "리뷰 내용은 1글자 이상 1000자 이하이어야 합니다."),
  INVALID_REVIEW_TITLE_LENGTH(400, "R003", "리뷰 제목은 1글자 이상 50자 이하이어야 합니다."),
  INVALID_REVIEW_DATE(400, "R004", "방문일은 오늘 이후일 수 없습니다."),
  REVIEW_LIKE_FIELD_CONTAINS_NULL_VALUE(400, "R005", "리뷰 좋아요 필드에 NULL값이 포함되어 있습니다."),
  REVIEW_PHOTO_FIELD_CONTAINS_NULL_VALUE(400, "R006", "리뷰 사진 필드에 NULL값이 포함되어 있습니다."),
  INVALID_REVIEW_PHOTO_PATH_LENGTH(400, "R007", "path는 1글자 이상 2083자 이하이어야 합니다."),
  INVALID_REVIEW_PHOTO_COUNT(400, "R008", "리뷰 사진은 최대 9개입니다."),
  REVIEW_NOT_FOUND(400, "R009", "존재하지 않는 리뷰입니다."),
  REVIEW_PHOTO_NOT_FOUND(400, "R010", "존재하지 않는 리뷰 사진입니다."),
  NO_PERMISSION_TO_UPDATE_REVIEW(404, "R011", "해당 리뷰를 수정할 수 없습니다."),
  INVALID_REVIEW_SORT_TYPE(400, "R012", "유효하지 않은 후기 정렬 조건입니다.");


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
