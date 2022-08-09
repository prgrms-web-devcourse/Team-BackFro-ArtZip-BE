package com.prgrms.artzip.review.dto.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewWithLikeData extends ReviewData {

  private Boolean isLiked;
  private Long likeCount;

  public ReviewWithLikeData(Long reviewId, LocalDate date, String title, String content,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isPublic, Boolean isLiked,
      Long likeCount) {
    super(reviewId, date, title, content, createdAt, updatedAt, isPublic);
    this.isLiked = isLiked;
    this.likeCount = likeCount;
  }
}
