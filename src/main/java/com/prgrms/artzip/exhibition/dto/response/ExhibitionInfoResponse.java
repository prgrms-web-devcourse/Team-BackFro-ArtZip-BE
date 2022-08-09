package com.prgrms.artzip.exhibition.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import java.time.LocalDate;
import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class ExhibitionInfoResponse extends ExhibitionBasicInfoResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private Boolean isLiked;
  private long likeCount;
  private long reviewCount;
  private String placeAddr;
  private Double lat;
  private Double lng;

  public ExhibitionInfoResponse(ExhibitionForSimpleQuery exhibitionForSimpleQuery) {
    super(exhibitionForSimpleQuery.getId(), exhibitionForSimpleQuery.getName(),
        exhibitionForSimpleQuery.getThumbnail());
    this.startDate = exhibitionForSimpleQuery.getPeriod().getStartDate();
    this.endDate = exhibitionForSimpleQuery.getPeriod().getEndDate();
    this.isLiked = exhibitionForSimpleQuery.getIsLiked();
    this.likeCount = exhibitionForSimpleQuery.getLikeCount();
    this.reviewCount = exhibitionForSimpleQuery.getReviewCount();
  }

  public ExhibitionInfoResponse(
      ExhibitionWithLocationForSimpleQuery exhibitionWithLocationForSimpleQuery) {
    super(exhibitionWithLocationForSimpleQuery.getId(),
        exhibitionWithLocationForSimpleQuery.getName(),
        exhibitionWithLocationForSimpleQuery.getThumbnail());

    this.startDate = exhibitionWithLocationForSimpleQuery.getPeriod().getStartDate();
    this.endDate = exhibitionWithLocationForSimpleQuery.getPeriod().getEndDate();
    this.isLiked = exhibitionWithLocationForSimpleQuery.getIsLiked();
    this.likeCount = exhibitionWithLocationForSimpleQuery.getLikeCount();
    this.reviewCount = exhibitionWithLocationForSimpleQuery.getReviewCount();
    this.placeAddr = exhibitionWithLocationForSimpleQuery.getLocation().getAddress();
    this.lat = exhibitionWithLocationForSimpleQuery.getLocation().getLatitude();
    this.lng = exhibitionWithLocationForSimpleQuery.getLocation().getLongitude();
  }
}
