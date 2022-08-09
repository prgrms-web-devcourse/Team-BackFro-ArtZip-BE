package com.prgrms.artzip.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentLikeResponse {
  private final Long commentId;
  private final Boolean isLiked;
  private final Long likeCount;

  @Builder
  public CommentLikeResponse(Long commentId, Boolean isLiked, Long likeCount) {
    this.commentId = commentId;
    this.isLiked = isLiked;
    this.likeCount = likeCount;
  }
}
