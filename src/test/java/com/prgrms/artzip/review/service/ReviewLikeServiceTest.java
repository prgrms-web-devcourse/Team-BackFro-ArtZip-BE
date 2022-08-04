package com.prgrms.artzip.review.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLikeId;
import com.prgrms.artzip.review.domain.repository.ReviewLikeRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
      .genre(Genre.FINEART)
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
        boolean isLiked = true;
        ReviewLikeId reviewLikeId = new ReviewLikeId(review.getId(), user.getId());

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doReturn(isLiked).when(reviewLikeRepository).existsById(reviewLikeId);

        reviewLikeService.updateReviewLike(user.getId(), review.getId());

        verify(reviewLikeRepository).deleteById(reviewLikeId);
        verify(reviewLikeRepository, never()).save(any());
        verify(reviewLikeRepository).countByReview(review);
      }

      @Test
      @DisplayName("현재 좋아요 off인 상태인 경우, 좋아요가 성공적으로 on")
      void testLikeCreation() {
        boolean isLiked = false;
        ReviewLikeId reviewLikeId = new ReviewLikeId(review.getId(), user.getId());

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doReturn(isLiked).when(reviewLikeRepository).existsById(reviewLikeId);

        reviewLikeService.updateReviewLike(user.getId(), review.getId());

        verify(reviewLikeRepository, never()).deleteById(reviewLikeId);
        verify(reviewLikeRepository).save(any());
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
}