package com.prgrms.artzip.exibition.dto;

import com.prgrms.artzip.exibition.domain.Period;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExhibitionForSimpleQuery {
  private Long exhibitionId;
  private String name;
  private String thumbnail;
  private Period period;
  private long likeCount;
  private long reviewCount;

  @Builder
  public ExhibitionForSimpleQuery(Long exhibitionId, String name, String thumbnail,
      Period period, long likeCount, long reviewCount) {
    this.exhibitionId = exhibitionId;
    this.name = name;
    this.thumbnail = thumbnail;
    this.period = period;
    this.likeCount = likeCount;
    this.reviewCount = reviewCount;
  }
}
