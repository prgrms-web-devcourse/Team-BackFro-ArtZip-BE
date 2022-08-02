package com.prgrms.artzip.review.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.review.dto.response.ReviewCreateResponse;
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

  @ApiOperation(value = "후기 생성", notes = "후기 등록을 요청합니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> createReview(
      @RequestParam(value = "userId") Long userId,
      @ApiParam(value = "등록할 후기 데이터", required = true)
      @Parameter(name = "data", schema = @Schema(type = "string", format = "binary"))
      @RequestPart(value = "data") ReviewCreateRequest request,
      @RequestPart(required = false) List<MultipartFile> files) {

    ReviewCreateResponse response = reviewService.createReview(userId, request, files);

    return ResponseEntity.created(URI.create("/api/v1/reviews/" + response.getReviewId()))
        .body(new ApiResponse(
            "후기 생성 완료",
            HttpStatus.CREATED.value(),
            response));
  }


}
