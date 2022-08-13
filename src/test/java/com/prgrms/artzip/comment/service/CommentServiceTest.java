package com.prgrms.artzip.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.domain.CommentLike;
import com.prgrms.artzip.comment.dto.request.CommentCreateRequest;
import com.prgrms.artzip.comment.dto.request.CommentUpdateRequest;
import com.prgrms.artzip.comment.dto.response.CommentInfo;
import com.prgrms.artzip.comment.dto.response.CommentLikeResponse;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.repository.CommentLikeRepository;
import com.prgrms.artzip.comment.repository.CommentRepository;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentService 테스트")
class CommentServiceTest {

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private CommentUtilService commentUtilService;

  @Mock
  private CommentLikeRepository commentLikeRepository;

  @InjectMocks
  private CommentService commentService;

  private final Role role = new Role(Authority.USER);

  private final User user = LocalUser.builder()
      .email("test@test.com")
      .nickname("안녕하세요")
      .password("1q2w3e4r!")
      .roles(List.of(role))
      .build();

  private final Exhibition exhibition = Exhibition.builder()
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

  private final Review review = Review.builder()
      .user(user)
      .exhibition(exhibition)
      .content("이것은 리뷰 본문입니다.")
      .title("이것은 리뷰 제목입니다.")
      .date(LocalDate.now())
      .isPublic(true)
      .build();

  @Test
  @DisplayName("댓글 다건 조회 테스트")
  void testGetComments() {
    //given
    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    List<Comment> parents = List.of(
         comment
    );
    Pageable pageable = PageRequest.of(0, 10);
    doReturn(new PageImpl(parents)).when(commentRepository)
        .getCommentsByReviewId(review.getId(), pageable);
    doReturn(new ArrayList<>()).when(commentRepository).getCommentsOfParents(
        parents.stream().map(Comment::getId).toList()
    );

    //when
    commentService.getCommentsByReviewId(review.getId(), user, pageable);

    //then
    verify(commentRepository).getCommentsByReviewId(review.getId(), pageable);
    verify(commentRepository).getCommentsOfParents(parents.stream().map(Comment::getId).toList());
  }

  @Test
  @DisplayName("대댓글 조회 테스트")
  void testGetChildren() {
    //Given
    Comment parent = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        parent,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        parent,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    List<Comment> childContent = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      Comment child = Comment.builder()
          .user(user)
          .review(review)
          .content("안녕")
          .build();
      ReflectionTestUtils.setField(
          child,
          BaseEntity.class,
          "createdAt",
          LocalDateTime.now(),
          LocalDateTime.class
      );
      ReflectionTestUtils.setField(
          child,
          BaseEntity.class,
          "updatedAt",
          LocalDateTime.now(),
          LocalDateTime.class
      );
      childContent.add(child);
    }
    Page<Comment> children = new PageImpl<>(childContent);
    Pageable pageable = PageRequest.of(0, 10);
    doReturn(parent).when(commentUtilService).getComment(0L);
    doReturn(children).when(commentRepository).getCommentsOfParent(0L, pageable);

    //when
    Page<CommentInfo> response = commentService.getChildren(0L, user, pageable);

    //then
    verify(commentUtilService).getComment(0L);
    verify(commentRepository).getCommentsOfParent(0L, pageable);
    assertThat(response.getContent()).hasSize(9);
  }

  @Test
  @DisplayName("댓글 생성 테스트")
  void testCreateComment() {
    //given
    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    doReturn(comment).when(commentRepository).save(Mockito.any(Comment.class));
    doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

    //when
    commentService.createComment(
        new CommentCreateRequest("안녕", null),
        review.getId(),
        user
    );

    //then
    verify(commentRepository).save(Mockito.any(Comment.class));
    verify(reviewRepository).findById(review.getId());
  }

  @Test
  @DisplayName("잘못된 리뷰의 댓글 생성 테스트")
  void testCreateCommentWithInvalidReview() {
    //given
    doReturn(Optional.empty()).when(reviewRepository).findById(9999L);

    //when //then
    assertThatThrownBy(() -> commentService.createComment(
        new CommentCreateRequest("안녕", null),
        9999L,
        user
    )).isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.REVIEW_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("잘못된 부모 댓글의 자식 댓글 생성 테스트")
  void testCreateCommentWithInvalidParent() {
    //given
    doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
    when(commentUtilService.getComment(9999L))
        .thenThrow(new NotFoundException(ErrorCode.COMMENT_NOT_FOUND));

    //when //then
    assertThatThrownBy(() -> commentService.createComment(
        new CommentCreateRequest("안녕", 9999L),
        review.getId(),
        user
    )).isInstanceOf(NotFoundException.class)
        .hasMessage(ErrorCode.COMMENT_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("자식 댓글의 자식 댓글 생성 테스트")
  void testCreateCommentOfChildComment() {
    //given
    Comment parent = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        parent,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        parent,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    Comment child = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .parent(parent)
        .build();
    ReflectionTestUtils.setField(
        child,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        child,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
    doReturn(child).when(commentUtilService).getComment(0L);

    //when //then
    assertThatThrownBy(() -> commentService.createComment(
        new CommentCreateRequest("안녕", 0L),
        review.getId(),
        user
    )).isInstanceOf(InvalidRequestException.class)
        .hasMessage(ErrorCode.CHILD_CANT_BE_PARENT.getMessage());
  }

  @Test
  @DisplayName("댓글 갱신 테스트")
  void testUpdateComment() {
    //given
    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    doReturn(comment).when(commentUtilService).getComment(0L);
    doReturn(new ArrayList<>()).when(commentRepository).getCommentsOfParents(List.of(0L));

    //when
    CommentResponse response = commentService.updateComment(new CommentUpdateRequest("반가워"), 0L,
        user);

    //then
    verify(commentUtilService).getComment(0L);
    verify(commentRepository).getCommentsOfParents(List.of(0L));
    assertThat(response).hasFieldOrPropertyWithValue("content", "반가워");
  }

  @Test
  @DisplayName("댓글 삭제 테스트")
  void testDeleteComment() {
    //given
    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    doReturn(comment).when(commentUtilService).getComment(0L);
    doReturn(new ArrayList<>()).when(commentRepository).getCommentsOfParents(List.of(0L));

    //when
    CommentResponse response = commentService.deleteComment(0L, user);

    //then
    verify(commentUtilService).getComment(0L);
    verify(commentRepository).getCommentsOfParents(List.of(0L));
    assertThat(response).hasFieldOrPropertyWithValue("isDeleted", true);
    assertThat(response).hasAllNullFieldsOrPropertiesExcept("createdAt", "isDeleted", "commentId",
        "children", "childrenCount", "likeCount", "isLiked");
  }

  @Test
  @DisplayName("유저가 작성한 댓글 개수 반환 테스트")
  void testGetCommentCountByUserId() {
    // given
    when(commentRepository.countByUserId(1L)).thenReturn(3L);

    // when
    Long commentCount = commentService.getCommentCountByUserId(1L);

    // then
    assertThat(commentCount).isEqualTo(3L);
    verify(commentRepository).countByUserId(1L);
  }

  @Test
  @DisplayName("댓글 좋아요 토글 테스트")
  void testToggleLikeComment() {
    //Given
    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    doReturn(comment).when(commentUtilService).getComment(0L);
    doReturn(Optional.empty()).when(commentLikeRepository)
        .getCommentLikeByCommentIdAndUserId(0L, null);
    doReturn(CommentLike.builder().comment(comment).user(user).build())
        .when(commentLikeRepository).save(Mockito.any(CommentLike.class));
    doReturn(1L).when(commentLikeRepository).countCommentLikeByCommentId(0L);

    //When
    CommentLikeResponse response = commentService.toggleCommentLike(0L, user);

    //then
    verify(commentUtilService).getComment(0L);
    verify(commentLikeRepository).getCommentLikeByCommentIdAndUserId(0L, null);
    verify(commentLikeRepository).save(Mockito.any(CommentLike.class));
    verify(commentLikeRepository).countCommentLikeByCommentId(0L);
    assertThat(response).hasFieldOrPropertyWithValue("commentId", 0L)
        .hasFieldOrPropertyWithValue("isLiked", true)
        .hasFieldOrPropertyWithValue("likeCount", 1L);
  }

  @Test
  @DisplayName("댓글 좋아요 취소 토글 테스트")
  void testToggleUnlikeComment() {
    //Given
    Comment comment = Comment.builder()
        .user(user)
        .review(review)
        .content("안녕")
        .build();
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "createdAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    ReflectionTestUtils.setField(
        comment,
        BaseEntity.class,
        "updatedAt",
        LocalDateTime.now(),
        LocalDateTime.class
    );
    doReturn(comment).when(commentUtilService).getComment(0L);
    doReturn(Optional.of(CommentLike.builder().comment(comment).user(user).build()))
        .when(commentLikeRepository).getCommentLikeByCommentIdAndUserId(0L, null);
    doNothing().when(commentLikeRepository).deleteCommentLikeByCommentIdAndUserId(0L, null);
    doReturn(0L).when(commentLikeRepository).countCommentLikeByCommentId(0L);

    //When
    CommentLikeResponse response = commentService.toggleCommentLike(0L, user);

    //then
    verify(commentUtilService).getComment(0L);
    verify(commentLikeRepository).getCommentLikeByCommentIdAndUserId(0L, null);
    verify(commentLikeRepository).deleteCommentLikeByCommentIdAndUserId(0L, null);
    verify(commentLikeRepository).countCommentLikeByCommentId(0L);
    assertThat(response).hasFieldOrPropertyWithValue("commentId", 0L)
        .hasFieldOrPropertyWithValue("isLiked", false)
        .hasFieldOrPropertyWithValue("likeCount", 0L);
  }
}