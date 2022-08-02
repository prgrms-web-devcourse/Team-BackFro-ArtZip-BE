package com.prgrms.artzip.exibition.dto.projection;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ExhibitionBasicForSimpleQuery {

  private Long id;
  private String name;
  private String thumbnail;
}
