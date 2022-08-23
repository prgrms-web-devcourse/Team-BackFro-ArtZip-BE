package com.prgrms.artzip.review.service;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.domain.repository.ReviewLikeRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewLikeServiceTest {

    @InjectMocks
    private ReviewLikeService reviewLikeService;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    private User user = new User("test@example.com", "Emily", List.of(new Role(Authority.USER)));
    private Exhibition exhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
            .description("이것은 전시회 설명입니다.")
            .latitude(36.22)
            .longitude(128.02)
            .area(Area.BUSAN)
            .place("미술관")
            .address("부산 동구 중앙대로 11")
            .inquiry("문의처 정보")
            .fee("성인 20,000원")
            .thumbnail("https://www.image-example.com")
            .url("https://www.example.com")
            .placeUrl("https://www.place-example.com")
            .build();
    private Review review = Review.builder()
            .user(user)
            .exhibition(exhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();

    @Nested
    @DisplayName("후기 좋아요 등록/해제")
    class ReviewLikeUpdateTest {

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("현재 좋아요 on인 상태인 경우, 좋아요가 성공적으로 off")
            void testLikeDeletion() {
                // given
                ReviewLike reviewLike = new ReviewLike(review, user);

                doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
                doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
                doReturn(Optional.of(reviewLike))
                        .when(reviewLikeRepository).findByReviewIdAndUserId(review.getId(), user.getId());

                // when
                reviewLikeService.updateReviewLike(user.getId(), review.getId());

                // then
                verify(reviewLikeRepository).delete(reviewLike);
                verify(reviewLikeRepository, never()).save(any());
                verify(reviewLikeRepository).countByReview(review);
            }

            @Test
            @DisplayName("현재 좋아요 off인 상태인 경우, 좋아요가 성공적으로 on")
            void testLikeCreation() {
                // given
                doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
                doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
                doReturn(Optional.empty())
                        .when(reviewLikeRepository).findByReviewIdAndUserId(review.getId(), user.getId());

                // when
                reviewLikeService.updateReviewLike(user.getId(), review.getId());

                // then
                verify(reviewLikeRepository, never()).delete(any());
                verify(reviewLikeRepository).save(any(ReviewLike.class));
                verify(reviewLikeRepository).countByReview(review);
            }
        }

        @Nested
        @DisplayName("실패")
        class Failure {

            @Test
            @DisplayName("존재하지 않는 user인 경우 NotFoundException 발생")
            void invokeUserNotFoundExceptionTest() {
                doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
                        .when(userRepository).findById(any());

                assertThatThrownBy(() -> {
                    reviewLikeService.updateReviewLike(user.getId(), review.getId());
                }).isInstanceOf(NotFoundException.class)
                        .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
            }

            @Test
            @DisplayName("존재하지 않는 review인 경우 NotFoundException 발생")
            void invokeReviewNotFoundExceptionTest() {
                doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
                doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
                        .when(reviewRepository).findById(review.getId());

                assertThatThrownBy(() -> {
                    reviewLikeService.updateReviewLike(user.getId(), review.getId());
                }).isInstanceOf(NotFoundException.class)
                        .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
            }

        }
    }

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