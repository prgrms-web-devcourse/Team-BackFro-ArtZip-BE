package com.prgrms.artzip.comment.controller;

import com.prgrms.artzip.comment.dto.request.CommentUpdateRequest;
import com.prgrms.artzip.comment.dto.response.CommentInfo;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"댓글 API"})
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

  private final CommentService commentService;

  @ApiOperation(value = "대댓글 다건 조회", notes = "부모 댓글의 자식 댓글들을 조회합니다.")
  @GetMapping("/{commentId}/children")
  public ResponseEntity<ApiResponse<PageResponse<CommentInfo>>> getChildren(
      @PathVariable Long commentId,
      @PageableDefault Pageable pageable
  ) {
    PageResponse<CommentInfo> children =
        new PageResponse<CommentInfo>(commentService.getChildren(commentId, pageable));
    ApiResponse<PageResponse<CommentInfo>> response =
        new ApiResponse<>("자식 댓글 조회 성공", HttpStatus.OK.value(), children);
    return ResponseEntity
        .ok(response);
  }

  @ApiOperation(value = "댓글 수정하기", notes = "댓글을 수정합니다.")
  @PatchMapping("/{commentId}")
  public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
      @PathVariable Long commentId,
      @RequestBody CommentUpdateRequest request
  ) {
    CommentResponse comment = commentService.updateComment(request, commentId);
    ApiResponse<CommentResponse> response =
        new ApiResponse<>("댓글 수정 성공", HttpStatus.OK.value(), comment);
    return ResponseEntity
        .ok(response);
  }

  @ApiOperation(value = "댓글 삭제하기", notes = "댓글을 삭제합니다.")
  @DeleteMapping("/{commentId}")
  public ResponseEntity<ApiResponse<CommentResponse>> deleteComment(
      @PathVariable Long commentId
  ) {
    CommentResponse comment = commentService.deleteComment(commentId);
    ApiResponse<CommentResponse> response =
        new ApiResponse<>("댓글 삭제 성공", HttpStatus.OK.value(), comment);
    return ResponseEntity
        .ok(response);
  }
}
