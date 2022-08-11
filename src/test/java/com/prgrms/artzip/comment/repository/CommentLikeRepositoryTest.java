package com.prgrms.artzip.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.domain.CommentLike;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QueryDslTestConfig.class})
class CommentLikeRepositoryTest {

  @Autowired
  CommentLikeRepository commentLikeRepository;
  @PersistenceContext
  EntityManager em;

  private User user;
  private Exhibition exhibition;
  private Review review;
  private Comment comment;

  @BeforeEach
  void setUp() {
    Role role = new Role(Authority.USER);
    em.persist(role);
    user = LocalUser.builder()
        .email("test@test.com")
        .nickname("안녕하세요")
        .password("1q2w3e4r!")
        .roles(List.of(role))
        .build();
    em.persist(user);
    exhibition = Exhibition.builder()
        .seq(32)
        .name("전시회 제목")
        .startDate(LocalDate.of(2022, 4, 11))
        .endDate(LocalDate.of(2022, 6, 2))
        .genre(Genre.SHOW)
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
    comment = Comment.builder()
        .user(user)
        .content("이것은 댓글 본문입니다.")
        .review(review)
        .build();
    em.persist(comment);
  }

  @Test
  @DisplayName("댓글 좋아요 테스트")
  void testLikeComment() {
    //Given
    commentLikeRepository.save(
        CommentLike.builder()
            .comment(comment)
            .user(user)
            .build()
    );

    //When
    Optional<CommentLike> commentLike = commentLikeRepository
        .getCommentLikeByCommentIdAndUserId(comment.getId(), user.getId());

    //Then
    assertThat(commentLike.isPresent()).isTrue();
  }

  @Test
  @DisplayName("댓글 좋아요 취소 테스트")
  void testUnlikeComment() {
    //Given
    commentLikeRepository.save(
        CommentLike.builder()
            .comment(comment)
            .user(user)
            .build()
    );

    //When
    commentLikeRepository.deleteCommentLikeByCommentIdAndUserId(comment.getId(), user.getId());
    Optional<CommentLike> commentLike = commentLikeRepository
        .getCommentLikeByCommentIdAndUserId(comment.getId(), user.getId());

    //Then
    assertThat(commentLike.isEmpty()).isTrue();
  }
}