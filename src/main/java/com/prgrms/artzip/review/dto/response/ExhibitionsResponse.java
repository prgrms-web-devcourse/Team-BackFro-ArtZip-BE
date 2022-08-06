package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.exhibition.dto.response.ExhibitionBasicInfoResponse;
import java.util.List;
import lombok.Getter;

@Getter
public class ExhibitionsResponse {

  private final List<ExhibitionBasicInfoResponse> exhibitions;

  public ExhibitionsResponse(List<ExhibitionBasicInfoResponse> exhibitions) {
    this.exhibitions = exhibitions;
  }
}
