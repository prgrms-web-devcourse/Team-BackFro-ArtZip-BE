package com.prgrms.artzip.exhibition.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class ExhibitionsAroundMeResponse {

  private List<ExhibitionAroundMeInfoResponse> exhibitions;

  public ExhibitionsAroundMeResponse(
      List<ExhibitionAroundMeInfoResponse> exhibitions) {
    this.exhibitions = exhibitions;
  }
}
