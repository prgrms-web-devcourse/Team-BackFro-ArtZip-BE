package com.prgrms.artzip.review.dto.projection;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewData {

  private Long reviewId;
  private LocalDate date;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isPublic;

  public ReviewData(Long reviewId, LocalDate date, String title, String content,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isPublic) {
    this.reviewId = reviewId;
    this.date = date;
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.isPublic = isPublic;
  }
}
