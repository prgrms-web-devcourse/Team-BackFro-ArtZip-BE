package com.prgrms.artzip.exibition.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import com.prgrms.artzip.exibition.service.ExhibitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @ApiOperation(value = "다가오는 전시회 조회", notes = "다가오는 전시회를 조회합니다.")
  @GetMapping("/upcoming")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfo>>> getUpcomingExhibitions(Pageable pageable) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("다가오는 전시회 조회 성공")
        .code(HttpStatus.OK.value())
        .data(new PageResponse(exhibitionService.getUpcomingExhibitions(pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "인기 많은 전시회 조회", notes = "인기 많은 전시회를 조회합니다.")
  @GetMapping("/mostlike")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfo>>> getMostLikeExhibitions(@RequestParam(value = "include-end", required = false, defaultValue = "true") boolean includeEnd, Pageable pageable) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("인기 많은 전시회 조회 성공")
        .code(HttpStatus.OK.value())
        .data(new PageResponse(exhibitionService.getMostLikeExhibitions(includeEnd, pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }
}
