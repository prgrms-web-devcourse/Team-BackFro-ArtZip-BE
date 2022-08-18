package com.prgrms.artzip.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.projection.CommentSimpleProjection;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@Import({QueryDslTestConfig.class})
class CommentRepositoryTest {

  @PersistenceContext
  EntityManager em;
  @Autowired
  CommentRepository commentRepository;

  private User user;
  private Exhibition exhibition;
  private Review review;

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
    for (int i = 0; i < 30; i++) {
      Comment comment = commentRepository.save(Comment.builder()
          .content(String.valueOf(i))
          .review(review)
          .user(user)
          .build());
      commentRepository.save(Comment.builder()
          .content(String.valueOf(i) + "의 자식1")
          .review(review)
          .user(user)
          .parent(comment)
          .build());
      commentRepository.save(Comment.builder()
          .content(String.valueOf(i) + "의 자식2")
          .review(review)
          .user(user)
          .parent(comment)
          .build());
    }
  }

  @Test
  @DisplayName("댓글 다건 조회 테스트")
  void testGetCommentsByReviewId() {
    //Given
    Pageable pageable0 = PageRequest.of(0, 20);
    Pageable pageable1 = PageRequest.of(1, 20);

    //When
    Page<CommentSimpleProjection> comments0 = commentRepository.getCommentsByReviewIdQ(review.getId(), user.getId(), pageable0);
    Page<CommentSimpleProjection> comments1 = commentRepository.getCommentsByReviewIdQ(review.getId(), user.getId(), pageable1);

    //Then
    assertThat(comments0.getContent()).hasSize(20);
    assertThat(comments1.getContent()).hasSize(10);
    assertThat(comments0.getContent().get(0).getUser())
        .hasFieldOrPropertyWithValue("nickname", user.getNickname());
  }

  @Test
  @DisplayName("여러 부모의 자식 댓글 조회 테스트")
  void testGetCommentsOfParents() {
    //Given
    Pageable pageable = PageRequest.of(0, 40);
    Page<CommentSimpleProjection> comments = commentRepository.getCommentsByReviewIdQ(review.getId(), user.getId(), pageable);
    List<Long> parentIds = comments.getContent().stream().map(CommentSimpleProjection::getCommentId).toList();

    //When
    List<Comment> children = commentRepository.getCommentsOfParents(parentIds);

    //Then
    assertThat(children).hasSize(60);
    parentIds.forEach(parentId -> {
      assertThat(children.stream().filter(
          child -> child.getParent()
              .getId()
              .equals(parentId)
      ))
          .hasSize(2);
    });
  }

  @Test
  @DisplayName("부모의 자식 댓글 조회 테스트")
  void testGetCommentsOfParent() {
    //Given
    Pageable pageable0 = PageRequest.of(0, 40);
    Pageable pageable1 = PageRequest.of(0, 10);
    Page<CommentSimpleProjection> comments = commentRepository.getCommentsByReviewIdQ(review.getId(), user.getId(), pageable0);

    //When
    Page<Comment> children = commentRepository
        .getCommentsOfParent(comments.getContent().get(0).getCommentId(), pageable1);

    //Then
    assertThat(children.getContent()).hasSize(2);
  }

}