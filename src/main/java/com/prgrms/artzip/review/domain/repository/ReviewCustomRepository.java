package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import java.util.Optional;

public interface ReviewCustomRepository {

  Optional<ReviewWithLikeData> findByReviewIdAndUserId(Long reviewId, Long userId);
}
