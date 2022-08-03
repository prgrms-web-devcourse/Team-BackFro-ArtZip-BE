package com.prgrms.artzip.review.service;

import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public Long getReviewCountByUserId(Long userId) {
        return reviewRepository.countByUserId(userId);
    }
}
