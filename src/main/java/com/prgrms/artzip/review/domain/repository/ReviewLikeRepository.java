package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

  Long countByReview(Review review);

  @Query("SELECT COUNT(rl) from ReviewLike rl WHERE rl.user.id = :userId")
  Long countByUserId(@Param("userId") Long userId);

  @Query("SELECT rl FROM ReviewLike rl"
      + " WHERE rl.review.id = :reviewId and rl.user.id = :userId")
  Optional<ReviewLike> findByReviewIdAndUserId(
      @Param("reviewId") Long reviewId, @Param("userId") Long userId);
}
