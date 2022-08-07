package com.prgrms.artzip.review.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
public class ReviewRepositoryTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private ReviewRepository reviewRepository;

  private Role userRole;
  private User user1, user2, user3;
  private Exhibition exhibition;
  private Review publicReview, privateReview, deletedReview;
  private ReviewLike reviewLike1, reviewLike2, reviewLike3;

  @BeforeEach
  void setUp() {
    userRole = new Role(Authority.USER);
    em.persist(userRole);

    user1 = new User("test1@example.com", "Emily", List.of(userRole));
    em.persist(user1);
    user2 = new User("test2@example.com", "Jung", List.of(userRole));
    em.persist(user2);
    user3 = new User("test3@example.com", "Bob", List.of(userRole));
    em.persist(user3);

    exhibition = Exhibition.builder()
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
    em.persist(exhibition);

    publicReview = Review.builder()
        .user(user1)
        .exhibition(exhibition)
        .content("이것은 리뷰 본문입니다.")
        .title("이것은 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(true)
        .build();
    em.persist(publicReview);
    privateReview = Review.builder()
        .user(user1)
        .exhibition(exhibition)
        .content("이것은 리뷰 본문입니다.")
        .title("이것은 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(false)
        .build();
    em.persist(privateReview);
    deletedReview = Review.builder()
        .user(user1)
        .exhibition(exhibition)
        .content("이것은 삭제된 리뷰 본문입니다.")
        .title("이것은 삭제된 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(true)
        .build();
    deletedReview.updateIdDeleted(true);
    em.persist(deletedReview);

    reviewLike1 = new ReviewLike(publicReview, user1);
    em.persist(reviewLike1);
    reviewLike2 = new ReviewLike(publicReview, user2);
    em.persist(reviewLike2);
    reviewLike3 = new ReviewLike(privateReview, user3);
    em.persist(reviewLike3);
  }

  @Nested
  @DisplayName("findByReviewIdAndUserId() 테스트")
  class FindByReviewIdAndUserIdTest {

    @Test
    @DisplayName("삭제된 리뷰를 조회하는 경우, null을 반환한다.")
    void testDeletedReview() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          deletedReview.getId(), user1.getId());

      assertThat(result.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("삭제되지 않은 리뷰를 조회하는 경우, 리뷰 정보를 반환한다.")
    void testNotDeletedReview() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          publicReview.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(publicReview.getId());
      assertThat(result.get().getDate()).isEqualTo(publicReview.getDate());
      assertThat(result.get().getTitle()).isEqualTo(publicReview.getTitle());
      assertThat(result.get().getContent()).isEqualTo(publicReview.getContent());
      assertThat(result.get().getCreatedAt()).isEqualTo(publicReview.getCreatedAt());
      assertThat(result.get().getUpdatedAt()).isEqualTo(publicReview.getUpdatedAt());
      assertThat(result.get().getIsLiked()).isEqualTo(true);
      assertThat(result.get().getIsPublic()).isEqualTo(publicReview.getIsPublic());
      assertThat(result.get().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("후기 작성자가 조회하는 경우, isPublic == true인 리뷰 정보를 반환한다.")
    void testPublicReviewWithWriter() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          publicReview.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(publicReview.getId());
      assertThat(result.get().getDate()).isEqualTo(publicReview.getDate());
      assertThat(result.get().getTitle()).isEqualTo(publicReview.getTitle());
      assertThat(result.get().getContent()).isEqualTo(publicReview.getContent());
      assertThat(result.get().getCreatedAt()).isEqualTo(publicReview.getCreatedAt());
      assertThat(result.get().getUpdatedAt()).isEqualTo(publicReview.getUpdatedAt());
      assertThat(result.get().getIsLiked()).isEqualTo(true);
      assertThat(result.get().getIsPublic()).isEqualTo(publicReview.getIsPublic());
      assertThat(result.get().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("후기 작성자가 조회하는 경우, isPublic == false인 리뷰 정보를 반환한다.")
    void testPrivateReviewWhitWriter() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          privateReview.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(privateReview.getId());
      assertThat(result.get().getDate()).isEqualTo(privateReview.getDate());
      assertThat(result.get().getTitle()).isEqualTo(privateReview.getTitle());
      assertThat(result.get().getContent()).isEqualTo(privateReview.getContent());
      assertThat(result.get().getCreatedAt()).isEqualTo(privateReview.getCreatedAt());
      assertThat(result.get().getUpdatedAt()).isEqualTo(privateReview.getUpdatedAt());
      assertThat(result.get().getIsLiked()).isEqualTo(false);
      assertThat(result.get().getIsPublic()).isEqualTo(privateReview.getIsPublic());
      assertThat(result.get().getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("user == null인 경우, isPublic == true인 리뷰 정보를 반환한다.")
    void testNullUser() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          publicReview.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(publicReview.getId());
      assertThat(result.get().getDate()).isEqualTo(publicReview.getDate());
      assertThat(result.get().getTitle()).isEqualTo(publicReview.getTitle());
      assertThat(result.get().getContent()).isEqualTo(publicReview.getContent());
      assertThat(result.get().getCreatedAt()).isEqualTo(publicReview.getCreatedAt());
      assertThat(result.get().getUpdatedAt()).isEqualTo(publicReview.getUpdatedAt());
      assertThat(result.get().getIsLiked()).isEqualTo(true);
      assertThat(result.get().getIsPublic()).isEqualTo(publicReview.getIsPublic());
      assertThat(result.get().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("user == null인 경우, isPublic == false인 리뷰 정보는 반환하지 않는다.")
    void testPrivateReview() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          privateReview.getId(), null);

      assertThat(result.isEmpty()).isTrue();
    }

  }

}
