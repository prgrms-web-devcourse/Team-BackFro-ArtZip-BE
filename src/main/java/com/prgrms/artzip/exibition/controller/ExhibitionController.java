package com.prgrms.artzip.exibition.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.exibition.dto.response.ExhibitionDetailInfo;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import com.prgrms.artzip.exibition.dto.response.ExhibitionLikeResult;
import com.prgrms.artzip.exibition.service.ExhibitionLikeService;
import com.prgrms.artzip.exibition.service.ExhibitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"exhibitions"})
@RestController
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExhibitionController {
  private final ExhibitionService exhibitionService;
  private final ExhibitionLikeService exhibitionLikeService;

  @ApiOperation(value = "다가오는 전시회 조회", notes = "다가오는 전시회를 조회합니다.")
  @GetMapping("/upcoming")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfo>>> getUpcomingExhibitions(@PageableDefault(size = 10) Pageable pageable) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("다가오는 전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(new PageResponse(exhibitionService.getUpcomingExhibitions(pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "인기 많은 전시회 조회", notes = "인기 많은 전시회를 조회합니다.")
  @GetMapping("/mostlike")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfo>>> getMostLikeExhibitions(@RequestParam(value = "include-end", required = false, defaultValue = "true") boolean includeEnd, @PageableDefault(size = 10) Pageable pageable) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("인기 많은 전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(new PageResponse(exhibitionService.getMostLikeExhibitions(includeEnd, pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  // 수정 필요!
  @ApiOperation(value = "전시회 상세 조회", notes = "전시회를 조회합니다.")
  @GetMapping("/{exhibitionId}")
  public ResponseEntity<ApiResponse<ExhibitionDetailInfo>> getExhibition(@PathVariable Long exhibitionId) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("전시회 조회 성공")
        .status(HttpStatus.OK.value())
        .data(exhibitionService.getExhibition(exhibitionId, null))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  // 기능이 정상적으로 동작할 수 없기에 주석 처리하였습니다.
  // @ApiOperation(value = "전시회 좋아요 수정", notes = "전시회에 대한 좋아요를 추가 또는 삭제합니다.")
  // @GetMapping("/{exhibitionId}/likes")
  public ResponseEntity<ApiResponse<ExhibitionLikeResult>> updateExhibitionLike(@PathVariable Long exhibitionId) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("전시회 좋아요 수정 성공")
        .status(HttpStatus.OK.value())
        .data(exhibitionLikeService.updateExhibitionLike(exhibitionId, null))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
