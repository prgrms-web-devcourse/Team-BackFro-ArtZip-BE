package com.prgrms.artzip.review.dto.response;

public class ReviewExhibitionBasicInfoResponse {

  private Long exhibitionId;
  private String name;
  private String thumbnail;

  public ReviewExhibitionBasicInfoResponse(Long exhibitionId, String name, String thumbnail) {
    this.exhibitionId = exhibitionId;
    this.name = name;
    this.thumbnail = thumbnail;
  }
}
