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
import lombok.NoArgsConstructor;

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
  private Long commentCount;
  private List<ReviewPhotoInfo> photos;

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
    this.commentCount = review.getCommentCount();
    this.photos = photos.stream().map(ReviewPhotoInfo::new).collect(Collectors.toList());
  }

  @Builder
  public ReviewsResponseForExhibitionDetail(Long reviewId,
      ReviewUserInfo user, LocalDate date, String title, String content,
      LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isEdited, Boolean isLiked,
      Boolean isPublic, Long likeCount, Long commentCount,
      List<ReviewPhotoInfo> photos) {
    this.reviewId = reviewId;
    this.user = user;
    this.date = date;
    this.title = title;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.isEdited = isEdited;
    this.isLiked = isLiked;
    this.isPublic = isPublic;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
    this.photos = photos;
  }
}
