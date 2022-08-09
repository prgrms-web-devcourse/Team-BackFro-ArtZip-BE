package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;

public class ReviewsResponse extends ReviewInfo {

  private ReviewUserInfo user;
  private ReviewExhibitionBasicInfoResponse exhibition;

  public ReviewsResponse(ReviewWithLikeAndCommentCount review, Page<CommentResponse> comments,
      List<ReviewPhoto> photos, User user, Exhibition exhibition) {
    super(review, photos, comments);
    this.user = new ReviewUserInfo(user);
    this.exhibition = new ReviewExhibitionBasicInfoResponse(
        exhibition.getId(),
        exhibition.getName(),
        exhibition.getThumbnail());
  }

}
