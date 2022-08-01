package com.prgrms.artzip.review.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.assertj.core.internal.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReviewTest {

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

  @Nested
  @DisplayName("Review entity 필드값 검증")
  class ReviewEntityFieldValueValidationTest {

    @Nested
    @DisplayName("null 값")
    class ValidateNotNullTest {

      @Test
      @DisplayName("user가 null이면 InvalidRequestException이 발생")
      void userTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(null)
                  .exhibition(exhibition)
                  .content("이것은 리뷰 본문입니다.")
                  .title("이것은 리뷰 제목입니다.")
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("exhibition이 null이면 InvalidRequestException이 발생")
      void exhibitionTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(null)
                  .content("이것은 리뷰 본문입니다.")
                  .title("이것은 리뷰 제목입니다.")
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("content가 null이면 InvalidRequestException이 발생")
      void contentTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content(null)
                  .title("이것은 리뷰 제목입니다.")
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("title이 null이면 InvalidRequestException이 발생")
      void titleTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content("이것은 리뷰 본문입니다.")
                  .title(null)
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("date가 null이면 InvalidRequestException이 발생")
      void dateTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content("이것은 리뷰 본문입니다.")
                  .title("이것은 리뷰 제목입니다.")
                  .date(null)
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE));
      }

      @Test
      @DisplayName("isPublic이 null이면 InvalidRequestException이 발생")
      void isPublicTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content("이것은 리뷰 본문입니다.")
                  .title("이것은 리뷰 제목입니다.")
                  .date(LocalDate.now())
                  .isPublic(null)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE));
      }
    }

    @Nested
    @DisplayName("String 빈 공백(white space)")
    class ValidateWhiteSpaceTest {

      @Test
      @DisplayName("content가 빈 공백이면 InvalidRequestException이 발생")
      void contentTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content("  ")
                  .title("이것은 리뷰 제목입니다.")
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_CONTENT_LENGTH));
      }

      @Test
      @DisplayName("title이 빈 공백이면 InvalidRequestException이 발생")
      void titleWhiteSpaceTest() {
        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content("이것은 리뷰 본문입니다.")
                  .title("    ")
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            });
        assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_TITLE_LENGTH));
      }
    }

    @Nested
    @DisplayName("string length")
    class ValidateStringLengthTest {

      @Test
      @DisplayName("content의 길이가 1000보다 크면 InvalidRequestException 발생")
      void contentGreaterThanMaxValue() {
        String content = RandomString.make(1001);

        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content(content)
                  .title("이것은 리뷰 제목입니다.")
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            }
        );
        assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_CONTENT_LENGTH));
      }

      @Test
      @DisplayName("title의 길이가 51보다 크면 InvalidRequestException 발생")
      void titleGreaterThanMaxValue() {
        String title = RandomString.make(51);

        InvalidRequestException exception = assertThrows(
            InvalidRequestException.class, () -> {
              Review.builder()
                  .user(user)
                  .exhibition(exhibition)
                  .content("이것은 리뷰 본문입니다.")
                  .title(title)
                  .date(LocalDate.now())
                  .isPublic(true)
                  .build();
            }
        );
        assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_TITLE_LENGTH));
      }
    }
  }

  @Nested
  @DisplayName("Date")
  class ValidateDate {

    @Test
    @DisplayName("date가 오늘 이후이면 InvalidRequestException 발생")
    void afterToday() {
      LocalDate dayAfterToday = LocalDate.now().plusDays(1);

      InvalidRequestException exception = assertThrows(
          InvalidRequestException.class, () -> {
            Review.builder()
                .user(user)
                .exhibition(exhibition)
                .content("이것은 리뷰 본문입니다.")
                .title("이것은 리뷰 제목입니다.")
                .date(dayAfterToday)
                .isPublic(true)
                .build();
          }
      );
      assertThat(exception.getErrorCode(), is(ErrorCode.INVALID_REVIEW_DATE));
    }
  }

}