package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewsResponseForExhibitionDetail {

  private Long reviewId;
  private ReviewUserInfo user;
  private LocalDate date;
  private String title;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isEdited;
  private Boolean isLiked;
  private Boolean isPublic;
  private Long likeCount;
  private List<ReviewPhotoInfo> photos;

  @Builder
  public ReviewsResponseForExhibitionDetail(ReviewWithLikeAndCommentCount review,
      List<ReviewPhoto> photos, User user) {
    this.reviewId = review.getReviewId();
    this.user = new ReviewUserInfo(user);
    this.date = review.getDate();
    this.title = review.getTitle();
    this.content = review.getContent();
    this.createdAt = review.getCreatedAt();
    this.updatedAt = review.getUpdatedAt();
    this.isEdited = review.getCreatedAt().isEqual(review.getUpdatedAt()) ? false : true;
    this.isLiked = review.getIsLiked();
    this.isPublic = review.getIsPublic();
    this.likeCount = review.getLikeCount();
    this.photos = photos.stream().map(ReviewPhotoInfo::new).collect(Collectors.toList());
  }
}
