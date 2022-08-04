package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.domain.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    Long countByReview(Review review);

    @Query("SELECT COUNT(rl) from ReviewLike rl WHERE rl.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
