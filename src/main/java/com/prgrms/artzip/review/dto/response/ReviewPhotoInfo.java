package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.review.domain.ReviewPhoto;
import lombok.Getter;

@Getter
public class ReviewPhotoInfo {
  private Long photoId;
  private String path;

  public ReviewPhotoInfo(ReviewPhoto reviewPhoto) {
    this.photoId = reviewPhoto.getId();
    this.path = reviewPhoto.getPath();
  }
}
