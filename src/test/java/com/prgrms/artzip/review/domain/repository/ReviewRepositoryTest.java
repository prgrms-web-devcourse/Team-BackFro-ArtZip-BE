package com.prgrms.artzip.review.domain.repository;

import static com.prgrms.artzip.exhibition.domain.enumType.Area.BUSAN;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.GYEONGGI;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.SEOUL;
import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@DataJpaTest
@Import({QueryDslTestConfig.class})
public class ReviewRepositoryTest {

  @Autowired
  private TestEntityManager em;

  @Autowired
  private ReviewRepository reviewRepository;

  private Role userRole;
  private User user1, user2, user3;
  private Exhibition exhibitionAtBusan, exhibitionAtSeoul, exhibitionAlreadyEnd;
  private Review publicReview1, publicReview2, publicReview3, privateReview, deletedReview;
  private ReviewLike reviewLike1, reviewLike2, reviewLike3;

  int commentCountOfPublicReview1 = 30;
  int commentCountOfPublicReview2 = 20;

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

    exhibitionAtBusan = Exhibition.builder()
        .seq(32)
        .name("전시회 at 부산")
        .startDate(LocalDate.now().plusDays(10))
        .endDate(LocalDate.now().plusDays(15))
        .genre(Genre.FINEART)
        .description("이것은 전시회 설명입니다.")
        .latitude(36.22)
        .longitude(128.02)
        .area(BUSAN)
        .place("미술관")
        .address("부산 동구 중앙대로 11")
        .inquiry("문의처 정보")
        .fee("성인 20,000원")
        .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg")
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    em.persist(exhibitionAtBusan);

    exhibitionAtSeoul = Exhibition.builder()
        .seq(33)
        .name("전시회 at 서울")
        .startDate(LocalDate.now().plusDays(3))
        .endDate(LocalDate.now().plusDays(5))
        .genre(Genre.FINEART)
        .description("이것은 전시회 설명입니다.")
        .latitude(37.22)
        .longitude(129.02)
        .area(SEOUL)
        .place("미술관")
        .address("서울 어딘가")
        .inquiry("문의처 정보")
        .fee("성인 20,000원")
        .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg")
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    em.persist(exhibitionAtSeoul);

    exhibitionAlreadyEnd = Exhibition.builder()
        .seq(34)
        .name("전시회 at 경기")
        .startDate(LocalDate.now().minusDays(6))
        .endDate(LocalDate.now().minusDays(3))
        .genre(Genre.FINEART)
        .description("이것은 전시회 설명입니다.")
        .latitude(37.22)
        .longitude(129.02)
        .area(GYEONGGI)
        .place("미술관")
        .address("경기도 성남시")
        .inquiry("문의처 정보")
        .fee("성인 20,000원")
        .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg")
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    em.persist(exhibitionAlreadyEnd);

    publicReview1 = Review.builder()
        .user(user1)
        .exhibition(exhibitionAtBusan)
        .content("publicReview1 - 이것은 리뷰 본문입니다.")
        .title("publicReview1 - 이것은 리뷰 제목입니다.")
        .date(LocalDate.now().minusDays(10))
        .isPublic(true)
        .build();
    em.persist(publicReview1);
    publicReview2 = Review.builder()
        .user(user2)
        .exhibition(exhibitionAtBusan)
        .content("publicReview2 - 이것은 리뷰 본문입니다.")
        .title("publicReview2 - 이것은 리뷰 제목입니다.")
        .date(LocalDate.now().minusDays(20))
        .isPublic(true)
        .build();
    em.persist(publicReview2);
    publicReview3 = Review.builder()
        .user(user2)
        .exhibition(exhibitionAtSeoul)
        .content("publicReview3 - 이것은 리뷰 본문입니다.")
        .title("publicReview3 - 이것은 리뷰 제목입니다.")
        .date(LocalDate.now().minusDays(30))
        .isPublic(true)
        .build();
    em.persist(publicReview3);
    privateReview = Review.builder()
        .user(user1)
        .exhibition(exhibitionAtBusan)
        .content("privateReview - 이것은 리뷰 본문입니다.")
        .title("privateReview - 이것은 리뷰 제목입니다.")
        .date(LocalDate.now().minusDays(30))
        .isPublic(false)
        .build();
    em.persist(privateReview);
    deletedReview = Review.builder()
        .user(user1)
        .exhibition(exhibitionAtBusan)
        .content("deletedReview - 이것은 삭제된 리뷰 본문입니다.")
        .title("deletedReview - 이것은 삭제된 리뷰 제목입니다.")
        .date(LocalDate.now().minusDays(40))
        .isPublic(true)
        .build();
    deletedReview.updateIdDeleted(true);
    em.persist(deletedReview);

    reviewLike1 = new ReviewLike(publicReview1, user1);
    em.persist(reviewLike1);
    reviewLike2 = new ReviewLike(publicReview1, user2);
    em.persist(reviewLike2);
    reviewLike3 = new ReviewLike(privateReview, user3);
    em.persist(reviewLike3);

    for (int i = 0; i < commentCountOfPublicReview1; i++) {
      Comment comment = Comment.builder()
          .content(String.valueOf(i))
          .review(publicReview1)
          .user(user1)
          .build();
      Comment.builder()
          .content(String.valueOf(i) + "의 자식1")
          .review(publicReview1)
          .user(user1)
          .parent(comment)
          .build();
      Comment.builder()
          .content(String.valueOf(i) + "의 자식2")
          .review(publicReview1)
          .user(user1)
          .parent(comment)
          .build();
    }

    for (int i = 0; i < commentCountOfPublicReview1; i++) {
      Comment parentComment = Comment.builder()
          .content(String.valueOf(i))
          .review(publicReview1)
          .user(user1)
          .build();
      em.persist(parentComment);
      Comment childrenComment1 = Comment.builder()
          .content(String.valueOf(i) + "의 자식1")
          .review(publicReview1)
          .user(user1)
          .parent(parentComment)
          .build();
      em.persist(childrenComment1);
      Comment childrenComment2 = Comment.builder()
          .content(String.valueOf(i) + "의 자식2")
          .review(publicReview1)
          .user(user1)
          .parent(parentComment)
          .build();
      em.persist(childrenComment2);
    }

    for (int i = 0; i < commentCountOfPublicReview2; i++) {
      Comment parentComment = Comment.builder()
          .content(String.valueOf(i))
          .review(publicReview2)
          .user(user1)
          .build();
      em.persist(parentComment);
      Comment childrenComment1 = Comment.builder()
          .content(String.valueOf(i) + "의 자식1")
          .review(publicReview2)
          .user(user1)
          .parent(parentComment)
          .build();
      em.persist(childrenComment1);
      Comment childrenComment2 = Comment.builder()
          .content(String.valueOf(i) + "의 자식2")
          .review(publicReview2)
          .user(user1)
          .parent(parentComment)
          .build();
      em.persist(childrenComment2);
    }
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
          publicReview1.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(publicReview1.getId());
      assertThat(result.get().getDate()).isEqualTo(publicReview1.getDate());
      assertThat(result.get().getTitle()).isEqualTo(publicReview1.getTitle());
      assertThat(result.get().getContent()).isEqualTo(publicReview1.getContent());
      assertThat(result.get().getIsLiked()).isEqualTo(true);
      assertThat(result.get().getIsPublic()).isEqualTo(publicReview1.getIsPublic());
      assertThat(result.get().getLikeCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("후기 작성자가 조회하는 경우, isPublic == true인 리뷰 정보를 반환한다.")
    void testPublicReviewWithWriter() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          publicReview1.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(publicReview1.getId());
      assertThat(result.get().getDate()).isEqualTo(publicReview1.getDate());
      assertThat(result.get().getTitle()).isEqualTo(publicReview1.getTitle());
      assertThat(result.get().getContent()).isEqualTo(publicReview1.getContent());
      assertThat(result.get().getIsLiked()).isEqualTo(true);
      assertThat(result.get().getIsPublic()).isEqualTo(publicReview1.getIsPublic());
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
      assertThat(result.get().getIsLiked()).isEqualTo(false);
      assertThat(result.get().getIsPublic()).isEqualTo(privateReview.getIsPublic());
      assertThat(result.get().getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("user == null인 경우, isPublic == true인 리뷰 정보를 반환한다.")
    void testNullUser() {
      Optional<ReviewWithLikeData> result = reviewRepository.findByReviewIdAndUserId(
          publicReview1.getId(), user1.getId());

      assertThat(result.isPresent()).isTrue();
      assertThat(result.get().getReviewId()).isEqualTo(publicReview1.getId());
      assertThat(result.get().getDate()).isEqualTo(publicReview1.getDate());
      assertThat(result.get().getTitle()).isEqualTo(publicReview1.getTitle());
      assertThat(result.get().getContent()).isEqualTo(publicReview1.getContent());
      assertThat(result.get().getIsLiked()).isEqualTo(true);
      assertThat(result.get().getIsPublic()).isEqualTo(publicReview1.getIsPublic());
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

  @Nested
  @DisplayName("findReviewsByExhibitionIdAndUserId() 테스트: 후기 다건 조회")
  class TestFindReviewsByExhibitionIdAndUserId {

    Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

    @Nested
    @DisplayName("exhibitionId == null인 경우, 삭제된 후기와 비공개 후기를 제외한 모든 후기를 조회")
    class ExhibitionIdIsNull {

      @Test
      @DisplayName("userId == null인 경우, isLiked(좋아요 여부)는 조회되지 않는다.")
      void testUserIdIsNull() {

        Page<ReviewWithLikeAndCommentCount> result = reviewRepository.findReviewsByExhibitionIdAndUserId(
            null, null, pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(3);
        List<ReviewWithLikeAndCommentCount> content = result.getContent();
        assertThat(content.get(0))
            .hasFieldOrPropertyWithValue("commentCount", 0L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 0L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview3.getId())
            .hasFieldOrPropertyWithValue("date", publicReview3.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview3.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview3.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
        assertThat(content.get(1))
            .hasFieldOrPropertyWithValue("commentCount", 60L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 0L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview2.getId())
            .hasFieldOrPropertyWithValue("date", publicReview2.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview2.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview2.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
        assertThat(content.get(2))
            .hasFieldOrPropertyWithValue("commentCount", 90L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 2L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview1.getId())
            .hasFieldOrPropertyWithValue("date", publicReview1.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview1.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview1.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
      }

      @Test
      @DisplayName("userId != null인 경우, isLiked(좋아요 여부)도 함께 조회된다.")
      void testUserIdInNotNull() {
        Page<ReviewWithLikeAndCommentCount> result = reviewRepository.findReviewsByExhibitionIdAndUserId(
            null, user1.getId(), pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(3);
        List<ReviewWithLikeAndCommentCount> content = result.getContent();
        assertThat(content.get(0))
            .hasFieldOrPropertyWithValue("commentCount", 0L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 0L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview3.getId())
            .hasFieldOrPropertyWithValue("date", publicReview3.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview3.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview3.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
        assertThat(content.get(1))
            .hasFieldOrPropertyWithValue("commentCount", 60L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 0L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview2.getId())
            .hasFieldOrPropertyWithValue("date", publicReview2.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview2.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview2.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
        assertThat(content.get(2))
            .hasFieldOrPropertyWithValue("commentCount", 90L)
            .hasFieldOrPropertyWithValue("isLiked", true)
            .hasFieldOrPropertyWithValue("likeCount", 2L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview1.getId())
            .hasFieldOrPropertyWithValue("date", publicReview1.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview1.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview1.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
      }

    }

    @Nested
    @DisplayName("exhibitionId != null인 경우, 해당 전시회에 대한 삭제된 후기와 비공개 후기를 제외한 모든 후기를 조회")
    class ExhibitionIdIsNotNull {

      @Test
      @DisplayName("userId == null인 경우, isLiked(좋아요 여부)는 조회되지 않는다.")
      void testUserIdIsNull() {

        Page<ReviewWithLikeAndCommentCount> result = reviewRepository.findReviewsByExhibitionIdAndUserId(
            exhibitionAtBusan.getId(), null, pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        List<ReviewWithLikeAndCommentCount> content = result.getContent();
        assertThat(content.get(0))
            .hasFieldOrPropertyWithValue("commentCount", 60L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 0L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview2.getId())
            .hasFieldOrPropertyWithValue("date", publicReview2.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview2.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview2.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
        assertThat(content.get(1))
            .hasFieldOrPropertyWithValue("commentCount", 90L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 2L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview1.getId())
            .hasFieldOrPropertyWithValue("date", publicReview1.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview1.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview1.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
      }

      @Test
      @DisplayName("userId != null인 경우, isLiked(좋아요 여부)도 함께 조회된다.")
      void testUserIdInNotNull() {
        Page<ReviewWithLikeAndCommentCount> result = reviewRepository.findReviewsByExhibitionIdAndUserId(
            exhibitionAtBusan.getId(), user1.getId(), pageable);

        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getTotalElements()).isEqualTo(2);
        List<ReviewWithLikeAndCommentCount> content = result.getContent();
        assertThat(content.get(0))
            .hasFieldOrPropertyWithValue("commentCount", 60L)
            .hasFieldOrPropertyWithValue("isLiked", false)
            .hasFieldOrPropertyWithValue("likeCount", 0L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview2.getId())
            .hasFieldOrPropertyWithValue("date", publicReview2.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview2.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview2.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
        assertThat(content.get(1))
            .hasFieldOrPropertyWithValue("commentCount", 90L)
            .hasFieldOrPropertyWithValue("isLiked", true)
            .hasFieldOrPropertyWithValue("likeCount", 2L)
            .hasFieldOrPropertyWithValue("reviewId", publicReview1.getId())
            .hasFieldOrPropertyWithValue("date", publicReview1.getDate())
            .hasFieldOrPropertyWithValue("title", publicReview1.getTitle())
            .hasFieldOrPropertyWithValue("content", publicReview1.getContent())
            .hasFieldOrPropertyWithValue("isPublic", true);
      }

    }

  }

}