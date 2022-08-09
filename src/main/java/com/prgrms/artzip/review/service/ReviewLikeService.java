package com.prgrms.artzip.review.service;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.domain.repository.ReviewLikeRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.review.dto.response.ReviewLikeUpdateResponse;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewLikeService {

  private final ReviewRepository reviewRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final UserRepository userRepository;

  @Transactional
  public ReviewLikeUpdateResponse updateReviewLike(final Long userId, final Long reviewId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

    Optional<ReviewLike> maybeReviewLike = reviewLikeRepository.findByReviewIdAndUserId(
        review.getId(), user.getId());
    boolean isLiked = maybeReviewLike.isPresent();

    maybeReviewLike.ifPresentOrElse(
        reviewLike -> reviewLikeRepository.delete(reviewLike),
        () -> reviewLikeRepository.save(new ReviewLike(review, user))
    );

    Long likeCount = reviewLikeRepository.countByReview(review);

    return ReviewLikeUpdateResponse.builder()
        .reviewId(review.getId())
        .likeCount(likeCount)
        .isLiked(!isLiked)
        .build();
  }

  @Transactional(readOnly = true)
  public Long getReviewLikeCountByUserId(Long userId) {
    return reviewLikeRepository.countByUserId(userId);
  }
}
