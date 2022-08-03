package com.prgrms.artzip.review.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(NON_NULL)
@Getter
public class ReviewIdResponse {

  private Long reviewId;

  public ReviewIdResponse(Long reviewId) {
    this.reviewId = reviewId;
  }
}
