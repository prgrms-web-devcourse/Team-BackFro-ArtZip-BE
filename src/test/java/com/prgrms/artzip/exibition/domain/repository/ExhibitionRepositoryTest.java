package com.prgrms.artzip.exibition.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@Import({QueryDslTestConfig.class})
@DisplayName("ExhibitionRepository 테스트")
class ExhibitionRepositoryTest {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private ExhibitionRepository exhibitionRepository;

  private User user1;
  private User user2;

  private Exhibition exhibitionAtBusan;
  private Exhibition exhibitionAtSeoul;
  private Exhibition exhibitionAlreadyEnd;

  @BeforeEach
  void setUp() {
    Role role = new Role(Authority.USER);
    em.persist(role);

    user1 = new User("test@example.com", "Emily", List.of(role));
    em.persist(user1);

    user2 = new User("tes2t@example.com", "Jerry", List.of(role));
    em.persist(user2);

    exhibitionAtBusan = Exhibition.builder()
        .seq(32)
        .name("전시회 at 부산")
        .startDate(LocalDate.now().plusDays(10))
        .endDate(LocalDate.now().plusDays(15))
        .genre(Genre.FINEART)
        .description("이것은 전시회 설명입니다.")
        .latitude(36.22)
        .longitude(128.02)
        .area(Area.BUSAN)
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
        .area(Area.SEOUL)
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
        .area(Area.GYEONGGI)
        .place("미술관")
        .address("경기도 성남시")
        .inquiry("문의처 정보")
        .fee("성인 20,000원")
        .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg")
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    em.persist(exhibitionAlreadyEnd);

    Review review = Review.builder()
        .user(user1)
        .exhibition(exhibitionAtBusan)
        .content("이것은 리뷰 본문입니다.")
        .title("이것은 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(true)
        .build();
    em.persist(review);

    em.persist(new ExhibitionLike(user1, exhibitionAtSeoul));
    em.persist(new ExhibitionLike(user1, exhibitionAtBusan));
    em.persist(new ExhibitionLike(user1, exhibitionAlreadyEnd));
    em.persist(new ExhibitionLike(user2, exhibitionAlreadyEnd));

    em.flush();
    em.clear();
  }

  @Nested
  @DisplayName("findUpcomingExhibitions() 테스트")
  class FindUpcomingExhibitionsTest {

    @Test
    @DisplayName("로그인하지 않은 상태에서 실제로 시작일이 빠른 전시회가 먼저 오는지 확인하는 테스트")
    void testFindUpcomingExhibitionWithoutAuthorization() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findUpcomingExhibitions(null, PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtSeoul = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionsPagingResult.getContent()).hasSize(2);
      assertThat(exhibitionAtSeoul)
          .hasFieldOrPropertyWithValue("name", "전시회 at 서울")
          .hasFieldOrPropertyWithValue("likeCount", 1L)
          .hasFieldOrPropertyWithValue("isLiked", false)
          .hasFieldOrPropertyWithValue("reviewCount", 0L);
    }

    @Test
    @DisplayName("로그인한 상태에서 실제로 시작일이 빠른 전시회가 먼저 오는지 확인하는 테스트")
    void testFindUpcomingExhibitionWithAuthorization() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findUpcomingExhibitions(user1.getId(), PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtSeoul = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionsPagingResult.getContent()).hasSize(2);
      assertThat(exhibitionAtSeoul)
          .hasFieldOrPropertyWithValue("name", "전시회 at 서울")
          .hasFieldOrPropertyWithValue("likeCount", 1L)
          .hasFieldOrPropertyWithValue("isLiked", true)
          .hasFieldOrPropertyWithValue("reviewCount", 0L);
    }
  }

  @Nested
  @DisplayName("findMostLikeExhibitions() 테스트")
  class FindMostLikeExhibitionsTest {

    @Test
    @DisplayName("로그인하지 않고 종료된 전시회 포함하여 인기 많은 전시회 조회 테스트")
    void testFindMostLikeExhibitionIncludeEndWithoutAuthorization() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findMostLikeExhibitions(null, true, PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtGyeonggi = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionsPagingResult.getContent()).hasSize(3);

      assertThat(exhibitionAtGyeonggi)
          .hasFieldOrPropertyWithValue("name", "전시회 at 경기")
          .hasFieldOrPropertyWithValue("likeCount", 2L)
          .hasFieldOrPropertyWithValue("isLiked", false)
          .hasFieldOrPropertyWithValue("reviewCount", 0L);
    }

    @Test
    @DisplayName("로그인 하고 종료된 전시회 제외하고 인기 많은 전시회 조회 테스트")
    void testFindMostLikeExhibitionExcludeEndWithAuthorization() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findMostLikeExhibitions(user1.getId(), false, PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtBusan = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionAtBusan)
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산")
          .hasFieldOrPropertyWithValue("likeCount", 1L)
          .hasFieldOrPropertyWithValue("isLiked", true)
          .hasFieldOrPropertyWithValue("reviewCount", 1L);
    }
  }

  @Nested
  @DisplayName("findExhibition() 테스트")
  class FindExhibitionTest {

    @Test
    @DisplayName("존재하지 않는 전시회 조회 테스트")
    void testFindEmptyExhibition() {
      Optional<ExhibitionDetailForSimpleQuery> exhibition = exhibitionRepository
          .findExhibition(null, 123431L);
      assertThat(exhibition).isEmpty();
    }

    @Test
    @DisplayName("전시회 조회 테스트")
    void testFindExhibition() {
      Optional<ExhibitionDetailForSimpleQuery> exhibition = exhibitionRepository
          .findExhibition(user2.getId(), exhibitionAlreadyEnd.getId());

      assertThat(exhibition).isNotEmpty();
      assertThat(exhibition.get())
          .hasFieldOrPropertyWithValue("seq", 34)
          .hasFieldOrPropertyWithValue("name", "전시회 at 경기")
          .hasFieldOrPropertyWithValue("inquiry", "문의처 정보")
          .hasFieldOrPropertyWithValue("url", "https://www.example.com")
          .hasFieldOrPropertyWithValue("placeUrl", "https://www.place-example.com")
          .hasFieldOrPropertyWithValue("isLiked", true)
          .hasFieldOrPropertyWithValue("likeCount", 2L);
    }
  }

  @Nested
  @DisplayName("findExhibitionsByQuery() 테스트")
  class FindExhibitionsByQueryTest {

    @Test
    @DisplayName("로그인 하지 않고 끝난 전시회 제외 하지 않고 검색 경우 태스트")
    void testWithEndExhibition() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findExhibitionsByQuery(null, "부산", true, PageRequest.of(0, 10));

      assertThat(exhibitionsPagingResult.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("로그인 하고 끝난 전시회 제외하고 검색 경우 태스트")
    void testWithOutEndExhibition() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findExhibitionsByQuery(user2.getId(), "전시회", false, PageRequest.of(0, 10));

      assertThat(exhibitionsPagingResult.getContent()).hasSize(2);
      assertThat(exhibitionsPagingResult.getContent().get(0))
          .hasFieldOrPropertyWithValue("isLiked", false);
    }
  }

  @Nested
  @DisplayName("findExhibitionsForReview() 테스트")
  class FindExhibitionsForReviewTest {

    @Test
    @DisplayName("검색어에 맞는 결과가 반환되는지 테스트")
    void testFindExhibitionsForReview() {
      List<ExhibitionBasicForSimpleQuery> exhibitions = exhibitionRepository.findExhibitionsForReview(
          "부산");

      assertThat(exhibitions).hasSize(1);
      assertThat(exhibitions.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산")
          .hasFieldOrPropertyWithValue("thumbnail",
              "http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg");
    }
  }

  @Nested
  @DisplayName("findUserLikeExhibitions() 테스트")
  class FindUserLikeExhibitionsTest {

    @Test
    @DisplayName("로그인 유저와 조회 대상인 유저가 일치하지 않는 경우")
    void testDifferentUser() {
      // user2 : 로그인 유저
      // user1 : 조회 대상
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findUserLikeExhibitions(user2.getId(), user1.getId(), PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(3);

      assertThat(contents.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 경기")
          .hasFieldOrPropertyWithValue("isLiked", true);

      assertThat(contents.get(1))
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산")
          .hasFieldOrPropertyWithValue("isLiked", false);
    }

    @Test
    @DisplayName("로그인 유저와 조회 대상인 유저가 일치하는 경우")
    void testSameUser() {
      // user1 : 로그인 유저, 조회 대상
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findUserLikeExhibitions(user1.getId(), user1.getId(), PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(3);

      for (ExhibitionBasicForSimpleQuery exhibition : contents) {
        assertThat(exhibition).hasFieldOrPropertyWithValue("isLiked", true);
      }
    }
  }

}