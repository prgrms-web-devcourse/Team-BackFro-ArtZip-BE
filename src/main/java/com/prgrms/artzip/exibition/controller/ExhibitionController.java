package com.prgrms.artzip.exibition.controller;

import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.exibition.service.ExhibitionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"exhibitions"})
@RestController
@RequestMapping("/api/v1/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {
  private final ExhibitionService exhibitionService;

  @ApiOperation(value = "다가오는 전시회 조회", notes = "다가오는 전시회를 10개씩 조회합니다.")
  @GetMapping("/upcoming")
  public ResponseEntity<PageResponse> getUpcomingExhibitions(Pageable pageable) {
    return ResponseEntity
        .ok()
        .body(new PageResponse(exhibitionService.getUpcomingExhibitions(pageable)));
  }
}
