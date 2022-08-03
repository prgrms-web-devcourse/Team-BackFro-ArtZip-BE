package com.prgrms.artzip.review.service;

import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Long getReviewCountByUserId(Long userId) {
        return reviewRepository.countByUser(userId);
    }
}
