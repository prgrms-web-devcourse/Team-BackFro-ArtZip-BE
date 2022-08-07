package com.prgrms.artzip.exhibition.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ExhibitionLikeResponse {

  private Long exhibitionId;
  private long likeCount;
  private Boolean isLiked;

  @Builder
  public ExhibitionLikeResponse(Long exhibitionId, long likeCount, Boolean isLiked) {
    this.exhibitionId = exhibitionId;
    this.likeCount = likeCount;
    this.isLiked = isLiked;
  }
}
