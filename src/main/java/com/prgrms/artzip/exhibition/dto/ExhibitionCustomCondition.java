package com.prgrms.artzip.exhibition.dto;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;


@Getter
public class ExhibitionCustomCondition {

  private Set<Area> areas;
  private Set<Month> months;
  private Set<Genre> genres;
  private Boolean includeEnd;

  @Builder
  public ExhibitionCustomCondition(
      Set<Area> areas, Set<Month> months,
      Set<Genre> genres, Boolean includeEnd) {
    this.areas = areas;
    this.months = months;
    this.genres = genres;
    this.includeEnd = includeEnd;
  }
}
