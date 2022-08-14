package com.prgrms.artzip.review.controller;

import com.prgrms.artzip.comment.dto.request.CommentCreateRequest;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.dto.response.CommentsResponse;
import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.exhibition.service.ExhibitionSearchService;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.review.dto.request.ReviewUpdateRequest;
import com.prgrms.artzip.review.dto.response.ExhibitionsResponse;
import com.prgrms.artzip.review.dto.response.ReviewCreateResponse;
import com.prgrms.artzip.review.dto.response.ReviewIdResponse;
import com.prgrms.artzip.review.dto.response.ReviewLikeUpdateResponse;
import com.prgrms.artzip.review.dto.response.ReviewResponse;
import com.prgrms.artzip.review.dto.response.ReviewsResponse;
import com.prgrms.artzip.review.service.ReviewLikeService;
import com.prgrms.artzip.review.service.ReviewService;
import com.prgrms.artzip.user.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"후기 API"})
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;
  private final ExhibitionSearchService exhibitionSearchService;
  private final ReviewLikeService reviewLikeService;
  private final CommentService commentService;

  @ApiOperation(value = "후기 생성", notes = "후기 등록을 요청합니다.")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse<ReviewCreateResponse>> createReview(
      @CurrentUser User user,
      @ApiParam(value = "등록할 후기 데이터", required = true)
      @Parameter(name = "data", schema = @Schema(type = "string", format = "binary"))
      @RequestPart(value = "data") @Valid ReviewCreateRequest request,
      @RequestPart(required = false) List<MultipartFile> files) {

    ReviewIdResponse response = reviewService.createReview(user.getId(), request, files);

    return ResponseEntity.created(URI.create("/api/v1/reviews/" + response.getReviewId()))
        .body(new ApiResponse(
            "후기 생성 완료",
            HttpStatus.CREATED.value(),
            response));
  }

  @ApiOperation(value = "후기 작성 시, 전시회 검색", notes = "후기 작성 시, 전시회를 '전시회 이름'으로 검색합니다.")
  @GetMapping("/search/exhibitions")
  public ResponseEntity<ApiResponse<ExhibitionsResponse>> getExhibitions(
      @ApiParam(value = "검색할 전시회 이름", required = true)
      @RequestParam(value = "query") String query) {

    ExhibitionsResponse response = new ExhibitionsResponse(
        exhibitionSearchService.getExhibitionsForReview(query)
    );

    ApiResponse apiResponse = ApiResponse.builder()
        .message("전시회 검색 성공")
        .status(HttpStatus.OK.value())
        .data(response)
        .build();

    return ResponseEntity.ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "후기 좋아요 등록/해제", notes = "후기 좋아요 등록/해제를 요청합니다.")
  @PatchMapping("{reviewId}/like")
  public ResponseEntity<ApiResponse<ReviewLikeUpdateResponse>> updateReviewLike(
      @CurrentUser User user,
      @ApiParam(value = "좋아요 등록/해제할 후기의 ID")
      @PathVariable(value = "reviewId") final Long reviewId) {

    ReviewLikeUpdateResponse response = reviewLikeService.updateReviewLike(user.getId(), reviewId);

    ApiResponse apiResponse = ApiResponse.builder()
        .message("후기 좋아요 등록/해제 성공")
        .status(HttpStatus.OK.value())
        .data(response)
        .build();

    return ResponseEntity.ok()
        .body(apiResponse);
  }

  @ApiOperation(value = "후기 댓글 다건 조회", notes = "후기의 댓글들을 조회합니다.")
  @GetMapping("/{reviewId}/comments")
  public ResponseEntity<ApiResponse<CommentsResponse>> getComments(
      @ApiParam(value = "조회할 후기의 ID")
      @PathVariable Long reviewId,
      @CurrentUser User user,
      @PageableDefault(
          sort = {"createdAt"},
          direction = Sort.Direction.DESC
      ) Pageable pageable
  ) {
    CommentsResponse comments = commentService.getCommentsByReviewId(reviewId, user, pageable);
    ApiResponse<CommentsResponse> response
        = new ApiResponse<>("댓글 다건 조회 성공", HttpStatus.OK.value(), comments);
    return ResponseEntity.ok(response);
  }

  @ApiOperation(value = "리뷰 댓글 생성", notes = "리뷰에 댓글을 생성합니다.")
  @PostMapping("/{reviewId}/comments")
  public ResponseEntity<ApiResponse<CommentResponse>> createComment(
      @ApiParam(value = "댓글 생성할 후기의 ID")
      @PathVariable Long reviewId,
      @RequestBody @Valid CommentCreateRequest request,
      @CurrentUser User user
  ) {
    CommentResponse comment = commentService.createComment(request, reviewId, user);
    ApiResponse<CommentResponse> response
        = new ApiResponse<>("댓글 생성 성공", HttpStatus.OK.value(), comment);
    return ResponseEntity
        .created(URI.create("/api/v1/comments/" + comment.getCommentId()))
        .body(response);
  }

  @ApiOperation(value = "후기 수정", notes = "후기 수정을 요청합니다.")
  @PatchMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ApiResponse> updateReview(
      @CurrentUser User user,
      @ApiParam(value = "수정할 후기의 ID")
      @PathVariable(value = "reviewId") Long reviewId,
      @Parameter(name = "data", schema = @Schema(type = "string", format = "binary"))
      @RequestPart(value = "data") @Valid ReviewUpdateRequest request,
      @RequestPart(required = false) List<MultipartFile> files) {

    ReviewIdResponse response = reviewService.updateReview(user.getId(), reviewId, request, files);

    return ResponseEntity.ok()
        .body(ApiResponse.builder()
            .message("후기 수정 성공")
            .status(HttpStatus.OK.value())
            .data(response)
            .build());
  }

  @ApiOperation(value = "후기 삭제", notes = "후기 삭제를 요청합니다.")
  @DeleteMapping("/{reviewId}")
  public ResponseEntity<ApiResponse<ReviewIdResponse>> removeReview(
      @CurrentUser User user,
      @ApiParam(value = "삭제할 후기의 ID")
      @PathVariable(value = "reviewId") Long reviewId) {

    ReviewIdResponse response = reviewService.removeReview(user, reviewId);

    return ResponseEntity.ok()
        .body(ApiResponse.<ReviewIdResponse>builder()
            .message("후기 삭제 성공")
            .status(HttpStatus.OK.value())
            .data(response)
            .build());
  }

  @ApiOperation(value = "후기 단건 조회", notes = "후기 단건 조회를 요청합니다.")
  @GetMapping("/{reviewId}")
  public ResponseEntity<ApiResponse<ReviewResponse>> getReview(
      @CurrentUser User user,
      @ApiParam(value = "조회할 후기의 ID")
      @PathVariable(value = "reviewId") Long reviewId) {

    ReviewResponse response = reviewService.getReview(user, reviewId);

    return ResponseEntity.ok()
        .body(ApiResponse.<ReviewResponse>builder()
            .message("후기 단건 조회 성공")
            .status(HttpStatus.OK.value())
            .data(response)
            .build());
  }

  @ApiOperation(value = "후기 다건 조회", notes = "후기 다건 조회를 요청합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<PageResponse<ReviewsResponse>>> getReview(
      @CurrentUser User user,
      @ApiParam(value = "전시회 ID")
      @RequestParam(value = "exhibitionId", required = false) Long exhibitionId,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    PageResponse<ReviewsResponse> response = reviewService.getReviews(user, exhibitionId, pageable);

    return ResponseEntity.ok()
        .body(ApiResponse.<PageResponse<ReviewsResponse>>builder()
            .message("후기 다건 조회 성공")
            .status(HttpStatus.OK.value())
            .data(response)
            .build());
  }

}
