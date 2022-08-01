package com.prgrms.artzip.exibition.dto.projection;

import com.prgrms.artzip.exibition.domain.Period;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ExhibitionForSimpleQuery extends ExhibitionBasicForSimpleQuery {

  private Period period;
  private long likeCount;
  private long reviewCount;
  
}
