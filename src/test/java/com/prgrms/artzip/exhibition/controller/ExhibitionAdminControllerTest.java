package com.prgrms.artzip.exhibition.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateOrUpdateRequest;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionSemiUpdateRequest;
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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class ExhibitionAdminControllerTest {

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
  @DisplayName("관리자 전시회 다건 조회 테스트 - 생성 일자 순 정렬")
  void testGetExhibitionsOrderByCreatedAt() throws Exception {
    //given //when //then
    mockMvc.perform(get("/api/v1/admin/exhibitions"))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 다건 조회 테스트 - 좋아요 수 순 정렬")
  void testGetExhibitionsOrderByLikeCount() throws Exception {
    //given // when //then
    mockMvc.perform(get("/api/v1/admin/exhibitions")
            .param("sort", "likeCount,DESC"))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 다건 조회 테스트 - 잘못된 정렬 기준")
  void testGetExhibitionsOrderByInvalidSort() throws Exception {
    //given // when //then
    mockMvc.perform(get("/api/v1/admin/exhibitions")
            .param("sort", "something wrong"))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 단건 조회 테스트")
  void testGetExhibition() throws Exception {
    //given // when //then
    mockMvc.perform(get("/api/v1/admin/exhibitions/{exhibitionId}", exhibition.getId()))
        .andExpect(status().isOk())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 생성 테스트 - 썸네일 O")
  void testCreateExhibition() throws Exception {
    //given
    ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
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
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE,
        "test1".getBytes());

    // when //then
    mockMvc.perform(multipart("/api/v1/admin/exhibitions/")
            .file("thumbnail", file.getBytes())
            .param("name", request.getName())
            .param("startDate", request.getStartDate().toString())
            .param("endDate", request.getEndDate().toString())
            .param("genre", request.getGenre().toString())
            .param("description", request.getDescription())
            .param("latitude", request.getLatitude().toString())
            .param("longitude", request.getLongitude().toString())
            .param("area", request.getArea().toString())
            .param("place", request.getPlace())
            .param("address", request.getAddress())
            .param("inquiry", request.getInquiry())
            .param("fee", request.getFee())
            .param("url", request.getUrl())
            .param("placeUrl", request.getPlaceUrl()))
        .andExpect(status().isSeeOther())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 생성 테스트 - 썸네일 X")
  void testCreateExhibitionWithoutThumbnail() throws Exception {
    //given
    ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
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
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();

    // when //then
    mockMvc.perform(multipart("/api/v1/admin/exhibitions")
            .param("name", request.getName())
            .param("startDate", request.getStartDate().toString())
            .param("endDate", request.getEndDate().toString())
            .param("genre", request.getGenre().toString())
            .param("description", request.getDescription())
            .param("latitude", request.getLatitude().toString())
            .param("longitude", request.getLongitude().toString())
            .param("area", request.getArea().toString())
            .param("place", request.getPlace())
            .param("address", request.getAddress())
            .param("inquiry", request.getInquiry())
            .param("fee", request.getFee())
            .param("url", request.getUrl())
            .param("placeUrl", request.getPlaceUrl()))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  // 허위 thumbnail값 amazonS3Remover로 remove 시 에러 발생함에 따라 주석차리
//  @Test
//  @DisplayName("관리자 전시회 수정 테스트")
//  void testUpdateExhibition() throws Exception {
//    //given
//    ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
//        .name("전시회 제목")
//        .startDate(LocalDate.of(2022, 4, 11))
//        .endDate(LocalDate.of(2022, 6, 2))
//        .genre(Genre.PAINTING)
//        .description("이것은 전시회 설명입니다.")
//        .latitude(36.22)
//        .longitude(128.02)
//        .area(Area.BUSAN)
//        .place("미술관")
//        .address("부산 동구 중앙대로 11")
//        .inquiry("문의처 정보")
//        .fee("성인 20,000원")
//        .url("https://www.example.com")
//        .placeUrl("https://www.place-example.com")
//        .build();
//    MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE,
//        "test1".getBytes());
//    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
//        "/api/v1/admin/exhibitions/{exhibitionId}", exhibition.getId());
//    builder.with(new RequestPostProcessor() {
//      @Override
//      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
//        request.setMethod("PUT");
//        return request;
//      }
//    });
//
//    // when //then
//    mockMvc.perform(builder
//            .file("thumbnail", file.getBytes())
//            .param("name", request.getName())
//            .param("startDate", request.getStartDate().toString())
//            .param("endDate", request.getEndDate().toString())
//            .param("genre", request.getGenre().toString())
//            .param("description", request.getDescription())
//            .param("latitude", request.getLatitude().toString())
//            .param("longitude", request.getLongitude().toString())
//            .param("area", request.getArea().toString())
//            .param("place", request.getPlace())
//            .param("address", request.getAddress())
//            .param("inquiry", request.getInquiry())
//            .param("fee", request.getFee())
//            .param("url", request.getUrl())
//            .param("placeUrl", request.getPlaceUrl()))
//        .andExpect(status().isSeeOther())
//        .andDo(print());
//  }

  @Test
  @DisplayName("관리자 전시회 수정 테스트 - 썸네일 X")
  void testUpdateExhibitionWithoutThumbnail() throws Exception {
    //given
    ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
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
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
        "/api/v1/admin/exhibitions/{exhibitionId}", exhibition.getId());
    builder.with(new RequestPostProcessor() {
      @Override
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setMethod("PUT");
        return request;
      }
    });

    // when //then
    mockMvc.perform(builder
            .param("name", request.getName())
            .param("startDate", request.getStartDate().toString())
            .param("endDate", request.getEndDate().toString())
            .param("genre", request.getGenre().toString())
            .param("description", request.getDescription())
            .param("latitude", request.getLatitude().toString())
            .param("longitude", request.getLongitude().toString())
            .param("area", request.getArea().toString())
            .param("place", request.getPlace())
            .param("address", request.getAddress())
            .param("inquiry", request.getInquiry())
            .param("fee", request.getFee())
            .param("url", request.getUrl())
            .param("placeUrl", request.getPlaceUrl()))
        .andExpect(status().isSeeOther())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 없는 전시회 수정 테스트")
  void testUpdateInvalidExhibition() throws Exception {
    //given
    ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
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
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
        "/api/v1/admin/exhibitions/{exhibitionId}", 9999L);
    builder.with(new RequestPostProcessor() {
      @Override
      public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        request.setMethod("PUT");
        return request;
      }
    });

    // when //then
    mockMvc.perform(builder
            .param("name", request.getName())
            .param("startDate", request.getStartDate().toString())
            .param("endDate", request.getEndDate().toString())
            .param("genre", request.getGenre().toString())
            .param("description", request.getDescription())
            .param("latitude", request.getLatitude().toString())
            .param("longitude", request.getLongitude().toString())
            .param("area", request.getArea().toString())
            .param("place", request.getPlace())
            .param("address", request.getAddress())
            .param("inquiry", request.getInquiry())
            .param("fee", request.getFee())
            .param("url", request.getUrl())
            .param("placeUrl", request.getPlaceUrl()))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 간이 수정 테스트")
  void testSemiUpdateExhibition() throws Exception {
    //given
    ExhibitionSemiUpdateRequest request = ExhibitionSemiUpdateRequest.builder()
        .description("바뀔 설명입니다.")
        .genre(Genre.SHOW)
        .build();

    // when //then
    mockMvc.perform(patch("/api/v1/admin/exhibitions/{exhibitionId}/semi", exhibition.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isSeeOther())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 간이 수정 테스트 - null값")
  void testSemiUpdateExhibitionWithNullValue() throws Exception {
    //given
    ExhibitionSemiUpdateRequest request = ExhibitionSemiUpdateRequest.builder()
        .build();

    // when //then
    mockMvc.perform(patch("/api/v1/admin/exhibitions/{exhibitionId}/semi", exhibition.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isSeeOther())
        .andDo(print());
  }

  @Test
  @DisplayName("관리자 전시회 삭제 테스트")
  void testDeleteExhibition() throws Exception {
    // given // when //then
    mockMvc.perform(delete("/api/v1/admin/exhibitions/{exhibitionId}", exhibition.getId()))
        .andExpect(status().isSeeOther())
        .andDo(print());
    mockMvc.perform(delete("/api/v1/admin/exhibitions/{exhibitionId}", exhibition.getId()))
        .andExpect(status().isBadRequest())
        .andDo(print());
  }
}