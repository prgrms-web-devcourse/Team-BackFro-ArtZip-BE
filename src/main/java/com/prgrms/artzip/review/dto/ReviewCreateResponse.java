package com.prgrms.artzip.review.dto;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@JsonInclude(NON_NULL)
@Getter
public class ReviewCreateResponse {

  private Long reviewId;

  public ReviewCreateResponse(Long reviewId) {
    this.reviewId = reviewId;
  }
}
