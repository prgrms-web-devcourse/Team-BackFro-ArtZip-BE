package com.prgrms.artzip.review.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QueryDslTestConfig.class})
class ReviewPhotoRepositoryTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private ReviewPhotoRepository reviewPhotoRepository;

  private Role userRole;
  private User user;
  private Exhibition exhibition;
  private Review review;

  @BeforeEach
  void setUp() {
    userRole = new Role(Authority.USER);
    em.persist(userRole);

    user = new User("test@example.com", "Emily", List.of(userRole));
    em.persist(user);

    exhibition = Exhibition.builder()
        .seq(32)
        .name("전시회 제목")
        .startDate(LocalDate.of(2022, 4, 11))
        .endDate(LocalDate.of(2022, 6, 2))
        .genre(Genre.PHOTO)
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
    em.persist(exhibition);

    review = Review.builder()
        .user(user)
        .exhibition(exhibition)
        .content("이것은 리뷰 본문입니다.")
        .title("이것은 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(true)
        .build();
    em.persist(review);
  }

  @Nested
  @DisplayName("countByReview() 테스트: 특정 review의 reviewPhoto 개수 반환")
  class CountByReviewTest {

    @Test
    @DisplayName("후기 사진이 없는 경우에 0을 반환")
    void testCountByReviewWithNoReviewPhoto() {
      Long likeCount = reviewPhotoRepository.countByReview(review);

      assertThat(likeCount).isEqualTo(0);
    }

    @Test
    @DisplayName("후기 사진이 5개인 경우에 5를 반환")
    void testCountByReviewWithReviewPhoto() {
      int expectedPhotoCount = 5;
      for (int i = 0; i < expectedPhotoCount; i++) {
        ReviewPhoto reviewPhoto = new ReviewPhoto(review, "https://www.review-photo-path");
        em.persist(reviewPhoto);
      }

      Long actualPhotoCount = reviewPhotoRepository.countByReview(review);

      assertThat(actualPhotoCount).isEqualTo(expectedPhotoCount);
    }

  }
}