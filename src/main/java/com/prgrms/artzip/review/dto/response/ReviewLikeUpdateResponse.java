package com.prgrms.artzip.review.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewLikeUpdateResponse {
  private final Long reviewId;
  private final Long likeCount;
  private final Boolean isLiked;

  @Builder
  public ReviewLikeUpdateResponse(Long reviewId, Long likeCount, Boolean isLiked) {
    this.reviewId = reviewId;
    this.likeCount = likeCount;
    this.isLiked = isLiked;
  }
}
