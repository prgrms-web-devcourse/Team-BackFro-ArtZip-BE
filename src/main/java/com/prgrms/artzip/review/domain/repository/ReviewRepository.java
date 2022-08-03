package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT COUNT(r) from Review r WHERE r.user.id = :userId")
    Long countByUser(@Param("userId") Long userId);
}
