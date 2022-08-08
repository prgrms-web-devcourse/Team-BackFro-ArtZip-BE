package com.prgrms.artzip.review.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.request.CommentCreateRequest;
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
import java.util.ArrayList;
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
class ReviewControllerTest {

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
  private String accessToken;
  private List<Comment> comments = new ArrayList<>();

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
    review = Review.builder()
        .user(user)
        .exhibition(exhibition)
        .content("이것은 리뷰 본문입니다.")
        .title("이것은 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(true)
        .build();
    em.persist(review);
    for (int i = 0; i < 17; i++) {
      Comment comment = Comment.builder()
          .content("이것은 댓글 본문입니다.")
          .review(review)
          .user(user)
          .build();
      comments.add(comment);
      em.persist(comment);
      for (int j = 0; j < 15; j++) {
        Comment child = Comment.builder()
            .content(String.valueOf(i))
            .review(review)
            .user(user)
            .parent(comment)
            .build();
        em.persist(child);
      }
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
  @DisplayName("후기 댓글 다건 조회 테스트")
  void testGetComments() throws Exception {
    //Given //When //Then
    mockMvc.perform(get("/api/v1/reviews/{reviewId}/comments", review.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("댓글 생성 테스트")
  void testCreateComment() throws Exception {
    //Given
    CommentCreateRequest request = new CommentCreateRequest("너 누구야", null);

    // When //Then
    mockMvc.perform(post("/api/v1/reviews/{reviewId}/comments", review.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @DisplayName("빈 본문으로 댓글 생성 테스트")
  void testCreateCommentWithBlankContent() throws Exception {
    //Given
    CommentCreateRequest request = new CommentCreateRequest("          ", null);

    // When //Then
    mockMvc.perform(post("/api/v1/reviews/{reviewId}/comments", review.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("자식 댓글 생성 테스트")
  void testCreateCommentOfParent() throws Exception {
    //Given
    CommentCreateRequest request = new CommentCreateRequest("너 누구야", comments.get(0).getId());

    // When //Then
    mockMvc.perform(post("/api/v1/reviews/{reviewId}/comments", review.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andDo(print());
  }

  @Test
  @DisplayName("존재하지 않는 부모의 자식 댓글 생성 테스트")
  void testCreateCommentOfInvalidParent() throws Exception {
    //Given
    CommentCreateRequest request = new CommentCreateRequest("너 누구야", 9999L);

    // When //Then
    mockMvc.perform(post("/api/v1/reviews/{reviewId}/comments", review.getId())
            .header("accessToken", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
}