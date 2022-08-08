package com.prgrms.artzip.exhibition.dto.projection;

import com.prgrms.artzip.exhibition.domain.vo.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ExhibitionWithLocationForSimpleQuery extends ExhibitionForSimpleQuery {
  
  private Location location;
}
