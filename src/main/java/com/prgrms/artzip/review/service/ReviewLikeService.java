package com.prgrms.artzip.review.service;

import com.prgrms.artzip.review.domain.repository.ReviewLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewLikeService {

    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional(readOnly = true)
    public Long getReviewLikeCountByUserId(Long userId) {
        return reviewLikeRepository.countByUserId(userId);
    }
}
