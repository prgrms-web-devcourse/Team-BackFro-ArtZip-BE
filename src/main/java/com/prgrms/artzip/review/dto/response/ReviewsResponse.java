package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewsResponse {

  private Long reviewId;
  private ReviewUserInfo user;
  private ReviewExhibitionBasicInfoResponse exhibition;
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

  public ReviewsResponse(ReviewWithLikeAndCommentCount review, List<ReviewPhoto> photos,
      User user, Exhibition exhibition) {
    this.reviewId = review.getReviewId();
    this.user = new ReviewUserInfo(user);
    this.exhibition = new ReviewExhibitionBasicInfoResponse(
        exhibition.getId(),
        exhibition.getName(),
        exhibition.getThumbnail());
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
