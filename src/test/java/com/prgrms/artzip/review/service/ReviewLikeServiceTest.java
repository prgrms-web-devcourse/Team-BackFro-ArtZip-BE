package com.prgrms.artzip.review.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.prgrms.artzip.review.domain.repository.ReviewLikeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceTest {

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @InjectMocks
    private ReviewLikeService reviewLikeService;

    @Test
    @DisplayName("유저가 좋아요 누른 리뷰 개수 반환 테스트")
    void testGetReviewLikeCountByUserId() {
        // given
        when(reviewLikeRepository.countByUserId(1L)).thenReturn(3L);
        // when
        Long reviewLikeCount = reviewLikeService.getReviewLikeCountByUserId(1L);
        // then
        assertThat(reviewLikeCount).isEqualTo(3L);
        verify(reviewLikeRepository).countByUserId(1L);
    }
}