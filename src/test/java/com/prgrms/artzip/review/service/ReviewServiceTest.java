package com.prgrms.artzip.review.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @DisplayName("유저가 작성한 후기 개수 반환")
    void testGetReviewCountByUserId() {
        // given
        when(reviewRepository.countByUserId(1L)).thenReturn(3L);
        // when
        Long reviewCount = reviewService.getReviewCountByUserId(1L);
        // then
        assertThat(reviewCount).isEqualTo(3L);
        verify(reviewRepository).countByUserId(1L);
    }
}