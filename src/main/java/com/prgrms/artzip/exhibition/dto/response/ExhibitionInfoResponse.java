package com.prgrms.artzip.exhibition.dto.response;

import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ExhibitionInfoResponse extends ExhibitionBasicInfoResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean isLiked;
  private long likeCount;
  private long reviewCount;

  public ExhibitionInfoResponse(ExhibitionForSimpleQuery exhibitionForSimpleQuery) {
    super(exhibitionForSimpleQuery.getId(), exhibitionForSimpleQuery.getName(),
        exhibitionForSimpleQuery.getThumbnail());
    this.startDate = exhibitionForSimpleQuery.getPeriod().getStartDate();
    this.endDate = exhibitionForSimpleQuery.getPeriod().getEndDate();
    this.isLiked = exhibitionForSimpleQuery.getIsLiked();
    this.likeCount = exhibitionForSimpleQuery.getLikeCount();
    this.reviewCount = exhibitionForSimpleQuery.getReviewCount();
  }
}
