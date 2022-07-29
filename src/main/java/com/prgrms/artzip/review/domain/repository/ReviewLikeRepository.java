package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.domain.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

}
