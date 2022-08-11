package com.prgrms.artzip.review.dto.projection;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewExhibitionInfo {

  private Long exhibitionId;
  private String name;
  private String thumbnail;
  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean isLiked;
  private long likeCount;
  private long reviewCount;

  public ReviewExhibitionInfo(Long exhibitionId, String name, String thumbnail,
      LocalDate startDate, LocalDate endDate, Boolean isLiked, long likeCount, long reviewCount) {
    this.exhibitionId = exhibitionId;
    this.name = name;
    this.thumbnail = thumbnail;
    this.startDate = startDate;
    this.endDate = endDate;
    this.isLiked = isLiked;
    this.likeCount = likeCount;
    this.reviewCount = reviewCount;
  }
}
