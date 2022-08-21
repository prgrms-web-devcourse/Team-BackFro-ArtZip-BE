package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

  Optional<ReviewWithLikeAndCommentCount> findReviewByReviewId(Long reviewId, Long userId);

  Page<ReviewWithLikeAndCommentCount> findReviews(
      Long exhibitionId, Long userId, Pageable pageable);

  Page<ReviewWithLikeAndCommentCount> findMyLikesReviews(
      Long currentUserId, Long targetUserId, Pageable pageable);

  Page<ReviewWithLikeAndCommentCount> findMyReviews(
      Long currentUserId, Long targetUserId, Pageable pageable);
}
