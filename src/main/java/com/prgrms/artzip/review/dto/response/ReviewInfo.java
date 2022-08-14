package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

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
  private Boolean isLiked;
  private Boolean isPublic;
  private Long likeCount;
  private List<ReviewPhotoInfo> photos;

  public ReviewInfo(Page<CommentResponse> comments,
      ReviewWithLikeAndCommentCount reviewData, List<ReviewPhoto> photos) {
    super(reviewData.getCommentCount(), comments);
    this.reviewId = reviewData.getReviewId();
    this.date = reviewData.getDate();
    this.title = reviewData.getTitle();
    this.content = reviewData.getContent();
    this.createdAt = reviewData.getCreatedAt();
    this.updatedAt = reviewData.getUpdatedAt();
    this.isEdited = reviewData.getCreatedAt().isEqual(reviewData.getUpdatedAt()) ? false : true;
    this.isLiked = reviewData.getIsLiked();
    this.isPublic = reviewData.getIsPublic();
    this.likeCount = reviewData.getLikeCount();
    this.photos = photos.stream().map(ReviewPhotoInfo::new).collect(Collectors.toList());
  }

}
