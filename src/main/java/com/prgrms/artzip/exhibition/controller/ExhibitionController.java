package com.prgrms.artzip.exhibition.controller;

import static java.util.Objects.isNull;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCustomConditionRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionDetailInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionLikeResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionsAroundMeResponse;
import com.prgrms.artzip.exhibition.service.ExhibitionLikeService;
import com.prgrms.artzip.exhibition.service.ExhibitionSearchService;
import com.prgrms.artzip.exhibition.service.ExhibitionService;
import com.prgrms.artzip.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"전시회 API"})
@RestController
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExhibitionController {

  private final ExhibitionService exhibitionService;
  private final ExhibitionLikeService exhibitionLikeService;
  private final ExhibitionSearchService exhibitionSearchService;

  @ApiOperation(value = "다가오는 전시회 조회", notes = "다가오는 전시회를 조회합니다.")
  @GetMapping("/upcoming")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getUpcomingExhibitions(
      @CurrentUser User user,
      @PageableDefault(page = 0, size = 8) Pageable pageable) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("다가오는 전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(new PageResponse(
            exhibitionService.getUpcomingExhibitions(isNull(user) ? null : user.getId(), pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "인기 많은 전시회 조회", notes = "인기 많은 전시회를 조회합니다.")
  @GetMapping("/mostlike")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getMostLikeExhibitions(
      @CurrentUser User user,
      @RequestParam(value = "include-end", required = false, defaultValue = "true") boolean includeEnd,
      @PageableDefault(page = 0, size = 8) Pageable pageable) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("인기 많은 전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(
            new PageResponse(
                exhibitionService.getMostLikeExhibitions(isNull(user) ? null : user.getId(),
                    includeEnd, pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "전시회 상세 조회", notes = "전시회를 조회합니다.")
  @GetMapping("/{exhibitionId}")
  public ResponseEntity<ApiResponse<ExhibitionDetailInfoResponse>> getExhibition(
      @CurrentUser User user,
      @PathVariable Long exhibitionId) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(exhibitionService.getExhibition(isNull(user) ? null : user.getId(), exhibitionId))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "전시회 좋아요 수정", notes = "전시회에 대한 좋아요를 추가 또는 삭제합니다.")
  @PatchMapping("/{exhibitionId}/likes")
  public ResponseEntity<ApiResponse<ExhibitionLikeResponse>> updateExhibitionLike(
      @CurrentUser User user, @PathVariable Long exhibitionId) {

    ApiResponse apiResponse = ApiResponse.builder()
        .message("전시회 좋아요 수정 성공")
        .status(HttpStatus.OK.value())
        .data(exhibitionLikeService.updateExhibitionLike(user, exhibitionId))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "전시회 검색", notes = "전시회를 이름으로 검색합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getExhibitionByQuery(
      @CurrentUser User user,
      String query,
      @RequestParam(value = "include-end", required = false, defaultValue = "true") boolean includeEnd,
      @PageableDefault(page = 0, size = 8) Pageable pageable) {

    ApiResponse apiResponse = ApiResponse.builder()
        .message("전시회 검색 성공")
        .status(HttpStatus.OK.value())
        .data(new PageResponse(
            exhibitionSearchService.getExhibitionsByQuery(isNull(user) ? null : user.getId(), query,
                includeEnd, pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "맞춤 전시회 조회", notes = "위치와 시기에 맞는 전시회들을 조회합니다.")
  @GetMapping("/custom")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getExhibitionsByCustomCondition(
      @CurrentUser User user,
      @ModelAttribute @Valid ExhibitionCustomConditionRequest exhibitionCustomConditionRequest,
      @RequestParam(value = "include-end", required = false, defaultValue = "true") boolean includeEnd,
      @PageableDefault(page = 0, size = 8) Pageable pageable) {

    ApiResponse apiResponse = ApiResponse.builder()
        .message("맞춤 전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(new PageResponse(
            exhibitionSearchService.getExhibitionsByCustomCondition(
                isNull(user) ? null : user.getId(), exhibitionCustomConditionRequest, includeEnd,
                pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "내 주변 전시회 조회", notes = "내 주변에 있는 전시회를 조회합니다.")
  @GetMapping("/aroundme")
  public ResponseEntity<ApiResponse<ExhibitionsAroundMeResponse>> getExhibitionsAroundMe(
      @CurrentUser User user,
      @RequestParam(value = "lat", required = false, defaultValue = "37.492001") double latitude,
      @RequestParam(value = "lng", required = false, defaultValue = "127.029704") double longitude,
      @RequestParam(value = "dist", required = false, defaultValue = "3") double distance
  ) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("내 주변의 전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(new ExhibitionsAroundMeResponse(
            exhibitionService.getExhibitionsAroundMe(isNull(user) ? null : user.getId(), latitude,
                longitude, distance)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
