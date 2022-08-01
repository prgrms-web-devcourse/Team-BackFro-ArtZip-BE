package com.prgrms.artzip.exibition.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrms.artzip.exibition.dto.response.ExhibitionBasicInfo;
import java.time.LocalDate;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@JsonInclude(NON_NULL)
public class ExhibitionInfo extends ExhibitionBasicInfo {
  private LocalDate startDate;
  private LocalDate endDate;
  private long likeCount;
  private long reviewCount;
  private Double lat;
  private Double lng;

  public ExhibitionInfo(Long exhibitionId, String name, String thumbnail,
      LocalDate startDate, LocalDate endDate, long likeCount, long reviewCount, Double lat,
      Double lng) {
    super(exhibitionId, name, thumbnail);
    this.startDate = startDate;
    this.endDate = endDate;
    this.likeCount = likeCount;
    this.reviewCount = reviewCount;
    this.lat = lat;
    this.lng = lng;
  }
}
