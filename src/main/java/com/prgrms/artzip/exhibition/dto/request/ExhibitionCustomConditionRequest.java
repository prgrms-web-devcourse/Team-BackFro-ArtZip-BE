package com.prgrms.artzip.exhibition.dto.request;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;


@Getter
public class ExhibitionCustomConditionRequest {

  @NotEmpty
  private List<Area> areas;

  @NotEmpty
  private List<Month> months;

  public ExhibitionCustomConditionRequest(
      List<Area> areas, List<Month> months) {
    this.areas = areas;
    this.months = months;
  }
}
