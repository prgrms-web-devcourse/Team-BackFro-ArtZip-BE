package com.prgrms.artzip.review.dto.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewWithLikeAndCommentCount extends ReviewWithLikeData {

  private Long commentCount;

  public ReviewWithLikeAndCommentCount(Long reviewId, LocalDate date, String title,
      String content, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isPublic,
      Boolean isLiked, Long likeCount, Long commentCount) {
    super(reviewId, date, title, content, createdAt, updatedAt, isPublic, isLiked, likeCount);
    this.commentCount = commentCount;
  }
}
