package com.prgrms.artzip.exibition.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ExhibitionLikeResult {
  private Long exhibitionId;
  private long likeCount;
  private Boolean isLiked;

  @Builder
  public ExhibitionLikeResult(Long exhibitionId, long likeCount, Boolean isLiked) {
    this.exhibitionId = exhibitionId;
    this.likeCount = likeCount;
    this.isLiked = isLiked;
  }
}
