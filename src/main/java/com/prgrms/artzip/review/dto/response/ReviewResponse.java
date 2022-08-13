package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import com.prgrms.artzip.user.domain.User;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@SuperBuilder
@Getter
public class ReviewResponse extends ReviewInfo {

  private ReviewUserInfo user;
  private ReviewExhibitionInfoResponse exhibition;

  public ReviewResponse(Page<CommentResponse> comments,
      ReviewWithLikeAndCommentCount reviewData, List<ReviewPhoto> photos,
      User user, ReviewExhibitionInfoResponse exhibition) {
    super(comments, reviewData, photos);
    this.user = new ReviewUserInfo(user);
    this.exhibition = exhibition;
  }
}
