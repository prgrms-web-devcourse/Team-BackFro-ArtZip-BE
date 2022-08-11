package com.prgrms.artzip.exhibition.dto.request;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;


@Getter
public class ExhibitionCustomConditionRequest {

  @NotEmpty
  private List<Area> areas;

  @NotEmpty
  private List<Month> months;

  @NotEmpty
  private List<Genre> genres;

  @Builder
  public ExhibitionCustomConditionRequest(
      List<Area> areas, List<Month> months, List<Genre> genres) {
    this.areas = areas;
    this.months = months;
    this.genres = genres;
  }
}
