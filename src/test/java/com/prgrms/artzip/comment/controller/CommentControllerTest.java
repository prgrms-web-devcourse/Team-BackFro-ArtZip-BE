package com.prgrms.artzip.comment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.request.CommentUpdateRequest;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class CommentControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private EntityManager em;
  @Autowired
  private ObjectMapper objectMapper;
  @Autowired
  private JwtService jwtService;

  private User user;
  private Exhibition exhibition;
  private Review review;
  private Comment comment;
  private String accessToken;

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
        .genre(Genre.PAINTING)
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
        .content("이것은 댓글 본문입니다.")
        .review(review)
        .user(user)
        .build();
    em.persist(comment);
    for (int i = 0; i < 17; i++) {
      Comment child = Comment.builder()
          .content(String.valueOf(i))
          .review(review)
          .user(user)
          .parent(comment)
          .build();
      em.persist(child);
    }
    accessToken = jwtService.createAccessToken(
        user.getId(),
        user.getEmail(),
        user.getRoles()
            .stream()
            .map(Role::getAuthority)
            .map(a -> new SimpleGrantedAuthority(a.toString()))
            .collect(Collectors.toList())
    );
  }

  @Test
  @DisplayName("자식 댓글 다건 조회 테스트 (No Login)")
  void testGetChildrenWithoutLogin() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/comments/{commentId}/children", comment.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("자식 댓글 다건 조회 테스트 (Login)")
  void testGetChildrenWithLogin() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/comments/{commentId}/children", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 수정 테스트 (No Login)")
  void testUpdateCommentWithoutLogin() throws Exception {
    //Given
    CommentUpdateRequest request = new CommentUpdateRequest("너 누구야");

    //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}", comment.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 수정 테스트 (Login)")
  void testUpdateCommentWithLogin() throws Exception {
    //Given
    CommentUpdateRequest request = new CommentUpdateRequest("너 누구야");

    //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("없는 댓글 수정 테스트")
  void testUpdateInvalidComment() throws Exception {
    //Given
    CommentUpdateRequest request = new CommentUpdateRequest("너 누구야");

    //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}", 9999L)
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("빈 본문으로 댓글 수정 테스트")
  void testUpdateCommentWithBlankContent() throws Exception {
    //Given
    CommentUpdateRequest request = new CommentUpdateRequest("   ");

    //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("너무 긴 본문으로 댓글 수정 테스트")
  void testUpdateCommentWithTooLongContent() throws Exception {
    //Given
    CommentUpdateRequest request = new CommentUpdateRequest("Long Content".repeat(50));

    //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 삭제 테스트 (No Login)")
  void testDeleteCommentWithoutLogin() throws Exception {
    //Given //When //Then
    mockMvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 삭제 테스트 (Login)")
  void testDeleteCommentWithLogin() throws Exception {
    //Given //When //Then
    mockMvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("없는 댓글 삭제 테스트")
  void testDeleteInvalidComment() throws Exception {
    //Given //When //Then
    mockMvc.perform(delete("/api/v1/comments/{commentId}", 9999L)
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 중복 삭제 테스트")
  void testDeleteCommentTwice() throws Exception {
    //Given //When //Then
    mockMvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
    mockMvc.perform(delete("/api/v1/comments/{commentId}", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 좋아요 토글 테스트 (No Login)")
  void testCommentLikeToggleWithoutLogin() throws Exception {
    //Given //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}/like", comment.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andDo(print());
    mockMvc.perform(patch("/api/v1/comments/{commentId}/like", comment.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andDo(print());
    mockMvc.perform(patch("/api/v1/comments/{commentId}/like", comment.getId())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 좋아요 토글 테스트 (Login)")
  void testCommentLikeToggleWithLogin() throws Exception {
    //Given //When //Then
    mockMvc.perform(patch("/api/v1/comments/{commentId}/like", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
    mockMvc.perform(patch("/api/v1/comments/{commentId}/like", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
    mockMvc.perform(patch("/api/v1/comments/{commentId}/like", comment.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }
}