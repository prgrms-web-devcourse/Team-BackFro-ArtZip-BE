package com.prgrms.artzip.exibition.dto.projection;

import com.prgrms.artzip.exibition.domain.Period;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExhibitionForSimpleQuery {
  private Long id;
  private String name;
  private String thumbnail;
  private Period period;
  private long likeCount;
  private long reviewCount;

  @Builder
  public ExhibitionForSimpleQuery(Long id, String name, String thumbnail,
      Period period, long likeCount, long reviewCount) {
    this.id = id;
    this.name = name;
    this.thumbnail = thumbnail;
    this.period = period;
    this.likeCount = likeCount;
    this.reviewCount = reviewCount;
  }
}
