package com.prgrms.artzip.exhibition.dto.projection;

import com.prgrms.artzip.exhibition.domain.vo.Period;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ExhibitionForSimpleQuery extends ExhibitionBasicForSimpleQuery {

  private Boolean isLiked;
  private Period period;
  private long likeCount;
  private long reviewCount;

}
