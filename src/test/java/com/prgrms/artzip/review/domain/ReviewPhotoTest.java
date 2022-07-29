package com.prgrms.artzip.review.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Area;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.Genre;
import com.prgrms.artzip.exibition.domain.Location;
import com.prgrms.artzip.exibition.domain.Period;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewPhotoTest {

  private User user = new User("test@example.com", "Emily");
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
      .thumbnail("http://image.com")
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
  @DisplayName("ReviewPhoto entity 필드값 검증")
  class ReviewPhotoEntityFieldValueValidation {

    @Nested
    @DisplayName("null 값")
    class ValidateNotNullTest {

      @Test
      @DisplayName("review가 null이면 InvalidRequestException 발생")
      void reviewTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              new ReviewPhoto(null, "https://www.review-photo.example.png");
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_PHOTO_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("path가 null이면 InvalidRequestException 발생")
      void userTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              new ReviewPhoto(review, null);
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_PHOTO_FIELD_CONTAINS_NULL_VALUE));
      }
    }


    @Nested
    @DisplayName("String 빈 공백(white space)")
    class ValidateWhiteSpaceTest {

      @Test
      @DisplayName("path가 빈 공백이면 InvalidRequestException이 발생")
      void pathTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              new ReviewPhoto(review, " ");
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_PHOTO_PATH_LENGTH));
      }
    }

    @Nested
    @DisplayName("string length")
    class ValidateStringLengthTest {

      @Test
      @DisplayName("path의 길이가 2083보다 크면 InvalidRequestException 발생")
      void pathGreaterThanMaxValue() {
        String path = RandomString.make(2084);

        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              new ReviewPhoto(review, path);
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_PHOTO_PATH_LENGTH));
      }
    }
  }
}