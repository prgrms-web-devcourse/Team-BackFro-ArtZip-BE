package com.prgrms.artzip.exibition.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class ExhibitionInfoResponseResponse extends ExhibitionBasicInfoResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean isLiked;
  private long likeCount;
  private long reviewCount;
  private Double lat;
  private Double lng;

  public ExhibitionInfoResponseResponse(ExhibitionForSimpleQuery exhibitionForSimpleQuery) {
    super(exhibitionForSimpleQuery.getId(), exhibitionForSimpleQuery.getName(),
        exhibitionForSimpleQuery.getThumbnail());
    this.startDate = exhibitionForSimpleQuery.getPeriod().getStartDate();
    this.endDate = exhibitionForSimpleQuery.getPeriod().getEndDate();
    this.isLiked = exhibitionForSimpleQuery.getIsLiked();
    this.likeCount = exhibitionForSimpleQuery.getLikeCount();
    this.reviewCount = exhibitionForSimpleQuery.getReviewCount();
  }
}
