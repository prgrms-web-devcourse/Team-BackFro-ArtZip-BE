package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.review.dto.projection.ReviewExhibitionInfo;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ReviewExhibitionInfoResponse extends ReviewExhibitionBasicInfoResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean isLiked;
  private long likeCount;
  private long reviewCount;

  public ReviewExhibitionInfoResponse(ReviewExhibitionInfo reviewExhibitionInfo) {
    super(reviewExhibitionInfo.getExhibitionId(), reviewExhibitionInfo.getName(), reviewExhibitionInfo.getThumbnail());
    this.startDate = reviewExhibitionInfo.getStartDate();
    this.endDate = reviewExhibitionInfo.getEndDate();
    this.isLiked = reviewExhibitionInfo.getIsLiked();
    this.likeCount = reviewExhibitionInfo.getLikeCount();
    this.reviewCount = reviewExhibitionInfo.getReviewCount();
  }
}
