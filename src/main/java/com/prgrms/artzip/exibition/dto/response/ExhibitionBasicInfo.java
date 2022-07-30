package com.prgrms.artzip.exibition.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ExhibitionBasicInfo {
  private Long exhibitionId;
  private String name;
  private String thumbnail;

  public ExhibitionBasicInfo(Long exhibitionId, String name, String thumbnail) {
    this.exhibitionId = exhibitionId;
    this.name = name;
    this.thumbnail = thumbnail;
  }
}
