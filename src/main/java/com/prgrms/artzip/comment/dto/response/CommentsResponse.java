package com.prgrms.artzip.comment.dto.response;

import com.prgrms.artzip.common.PageResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CommentsResponse {
  private final PageResponse<CommentResponseQ> comments;
  private final Integer commentCount;

  public CommentsResponse(
      PageResponse<CommentResponseQ> comments, Integer commentCount) {
    this.comments = comments;
    this.commentCount = commentCount;
  }
}
