package com.prgrms.artzip.review.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ReviewInfo extends ReviewCommentInfo {

  private Long reviewId;
  private LocalDate date;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isEdited;
  private Boolean isLiked;    // 로그인 안한 경우는 false
  private Boolean isPublic;
  private Long likeCount;
  private List<ReviewPhotoInfo> photos;
}
