package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewCustomRepository {

  Optional<ReviewWithLikeData> findByReviewIdAndUserId(Long reviewId, Long userId);

  Page<ReviewWithLikeAndCommentCount> findReviewsByExhibitionIdAndUserId(
      Long exhibitionId, Long userId, Pageable pageable);

  Page<ReviewWithLikeAndCommentCount> findReviewsByCurrentUserIdAndTargetUserId(
      Long currentUserId, Long targetUserId, Pageable pageable);
}
