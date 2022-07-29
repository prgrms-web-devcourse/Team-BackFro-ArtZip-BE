package com.prgrms.artzip.review.domain.repository;

import com.prgrms.artzip.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
