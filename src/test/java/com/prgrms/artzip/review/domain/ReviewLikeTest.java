package com.prgrms.artzip.review.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewLikeTest {

  private User user = new User("test@example.com", "Emily", List.of(new Role(Authority.USER)));
  private Exhibition exhibition = Exhibition.builder()
      .seq(32)
      .name("전시회 제목")
      .startDate(LocalDate.of(2022, 4, 11))
      .endDate(LocalDate.of(2022, 6, 2))
      .genre(Genre.MEDIA)
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
  @DisplayName("ReviewLike entity 필드값 검증")
  class ReviewLikeEntityFieldValueValidation {

    @Nested
    @DisplayName("null 값")
    class ValidateNotNullTest {

      @Test
      @DisplayName("review가 null이면 InvalidRequestException 발생")
      void reviewTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              new ReviewLike(null, user);
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_LIKE_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("user가 null이면 InvalidRequestException 발생")
      void userTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              new ReviewLike(review, null);
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_LIKE_FIELD_CONTAINS_NULL_VALUE));
      }
    }
  }
}