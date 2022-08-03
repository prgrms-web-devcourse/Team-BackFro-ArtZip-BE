package com.prgrms.artzip.review.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.exibition.service.ExhibitionSearchService;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.review.dto.response.ExhibitionsResponse;
import com.prgrms.artzip.review.dto.response.ReviewIdResponse;
import com.prgrms.artzip.review.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final ExhibitionSearchService exhibitionSearchService;

  @ApiOperation(value = "후기 생성", notes = "후기 등록을 요청합니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> createReview(
      @RequestParam(value = "userId") Long userId,
      @ApiParam(value = "등록할 후기 데이터", required = true)
      @Parameter(name = "data", schema = @Schema(type = "string", format = "binary"))
      @RequestPart(value = "data") ReviewCreateRequest request,
      @RequestPart(required = false) List<MultipartFile> files) {

    ReviewIdResponse response = reviewService.createReview(userId, request, files);

    return ResponseEntity.created(URI.create("/api/v1/reviews/" + response.getReviewId()))
        .body(new ApiResponse(
            "후기 생성 완료",
            HttpStatus.CREATED.value(),
            response));
  }

  @ApiOperation(value = "후기 작성 시, 전시회 검색", notes = "후기 작성 시, 전시회를 '전시회 이름'으로 검색합니다.")
  @GetMapping("/search/exhibitions")
  public ResponseEntity<ApiResponse> getExhibitions(
      @ApiParam(value = "검색할 전시회 이름", required = true)
      @RequestParam(value = "query") String query) {

    ExhibitionsResponse response = new ExhibitionsResponse(
        exhibitionSearchService.getExhibitionsForReview(query)
    );

    return ResponseEntity.ok()
        .body(ApiResponse.builder()
            .message("전시회 검색 성공")
            .status(HttpStatus.OK.value())
            .data(response)
            .build()
        );
  }
}
