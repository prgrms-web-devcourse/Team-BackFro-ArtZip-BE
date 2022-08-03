package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.exibition.dto.response.ExhibitionBasicInfo;
import java.util.List;
import lombok.Getter;

@Getter
public class ExhibitionsResponse {

  private final List<ExhibitionBasicInfo> exhibitions;

  public ExhibitionsResponse(List<ExhibitionBasicInfo> exhibitions) {
    this.exhibitions = exhibitions;
  }
}
