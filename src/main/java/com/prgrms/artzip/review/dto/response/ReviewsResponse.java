package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import com.prgrms.artzip.user.domain.User;
import java.util.List;
import org.springframework.data.domain.Page;

public class ReviewsResponse extends ReviewInfo {

  private ReviewUserInfo user;
  private ReviewExhibitionBasicInfoResponse exhibition;

  public ReviewsResponse(Long commentCount, Page<CommentResponse> comments,
      ReviewWithLikeData reviewData, List<ReviewPhoto> photos,
      User user, ReviewExhibitionBasicInfoResponse exhibition) {
    super(commentCount, comments, reviewData, photos);
    this.user = new ReviewUserInfo(user);
    this.exhibition = exhibition;
  }

}
