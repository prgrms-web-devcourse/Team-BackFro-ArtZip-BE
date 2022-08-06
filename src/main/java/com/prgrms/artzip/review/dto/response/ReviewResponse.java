package com.prgrms.artzip.review.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@JsonInclude(Include.NON_NULL)
public class ReviewResponse extends ReviewInfo {

  private ReviewUserInfo user;
  private ExhibitionInfo exhibition;
}
