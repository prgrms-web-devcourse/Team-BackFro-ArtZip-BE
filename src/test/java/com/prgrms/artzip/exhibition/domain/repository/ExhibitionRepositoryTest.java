package com.prgrms.artzip.exhibition.domain.repository;

import static com.prgrms.artzip.exhibition.domain.enumType.Area.BUSAN;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.GYEONGGI;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.SEOUL;
import static com.prgrms.artzip.exhibition.domain.enumType.Genre.INSATALLATION;
import static com.prgrms.artzip.exhibition.domain.enumType.Genre.MEDIA;
import static com.prgrms.artzip.exhibition.domain.enumType.Genre.PHOTO;
import static com.prgrms.artzip.exhibition.domain.enumType.Genre.SHOW;
import static com.prgrms.artzip.exhibition.domain.enumType.Month.DEC;
import static com.prgrms.artzip.exhibition.domain.enumType.Month.JAN;
import static com.prgrms.artzip.exhibition.domain.enumType.Month.JUN;
import static com.prgrms.artzip.exhibition.domain.enumType.Month.MAR;
import static com.prgrms.artzip.exhibition.domain.enumType.Month.MAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.ExhibitionLike;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import com.prgrms.artzip.exhibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.dto.projection.ReviewExhibitionInfo;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

@DataJpaTest
@Import({QueryDslTestConfig.class})
@DisplayName("ExhibitionRepository 테스트")
class ExhibitionRepositoryTest {

  @PersistenceContext
  private EntityManager em;

  @Autowired
  private ExhibitionRepository exhibitionRepository;

  @Nested
  @DisplayName("findUpcomingExhibitions() 테스트")
  class FindUpcomingExhibitionsTest {

    private User user1;

    @BeforeEach
    void setUp() {
      Role role = new Role(Authority.USER);
      em.persist(role);

      user1 = new User("test@example.com", "Emily", List.of(role));
      em.persist(user1);

      User user2 = new User("tes2t@example.com", "Jerry", List.of(role));
      em.persist(user2);

      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.now().plusDays(10))
          .endDate(LocalDate.now().plusDays(15))
          .genre(INSATALLATION)
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

      Exhibition exhibitionAtSeoul = Exhibition.builder()
          .seq(33)
          .name("전시회 at 서울")
          .startDate(LocalDate.now().plusDays(3))
          .endDate(LocalDate.now().plusDays(5))
          .genre(INSATALLATION)
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

      Exhibition exhibitionAlreadyEnd = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(6))
          .endDate(LocalDate.now().minusDays(3))
          .genre(INSATALLATION)
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

    private User user1;

    @BeforeEach
    void setUp() {
      Role role = new Role(Authority.USER);
      em.persist(role);

      user1 = new User("test@example.com", "Emily", List.of(role));
      em.persist(user1);

      User user2 = new User("tes2t@example.com", "Jerry", List.of(role));
      em.persist(user2);

      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.now().plusDays(10))
          .endDate(LocalDate.now().plusDays(15))
          .genre(INSATALLATION)
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

      Exhibition exhibitionAlreadyEnd = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(6))
          .endDate(LocalDate.now().minusDays(3))
          .genre(INSATALLATION)
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

      Review review = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      em.persist(review);

      em.persist(new ExhibitionLike(user1, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user1, exhibitionAlreadyEnd));
      em.persist(new ExhibitionLike(user2, exhibitionAlreadyEnd));

      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("로그인하지 않고 종료된 전시회 포함하여 인기 많은 전시회 조회 테스트")
    void testFindMostLikeExhibitionIncludeEndWithoutAuthorization() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findMostLikeExhibitions(null, true, PageRequest.of(0, 10));

      assertThat(exhibitionsPagingResult.getContent()).hasSize(2);

      ExhibitionForSimpleQuery exhibitionAtGyeonggi = exhibitionsPagingResult.getContent().get(0);
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

    private User user2;
    private Exhibition exhibitionAlreadyEnd;

    @BeforeEach
    void setUp() {
      Role role = new Role(Authority.USER);
      em.persist(role);

      User user1 = new User("test@example.com", "Emily", List.of(role));
      em.persist(user1);

      user2 = new User("tes2t@example.com", "Jerry", List.of(role));
      em.persist(user2);

      exhibitionAlreadyEnd = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(6))
          .endDate(LocalDate.now().minusDays(3))
          .genre(INSATALLATION)
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

      em.persist(new ExhibitionLike(user1, exhibitionAlreadyEnd));
      em.persist(new ExhibitionLike(user2, exhibitionAlreadyEnd));

      em.flush();
      em.clear();
    }


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
      Optional<ExhibitionDetailForSimpleQuery> exhibitionAtGyeonggi = exhibitionRepository
          .findExhibition(user2.getId(), exhibitionAlreadyEnd.getId());

      assertThat(exhibitionAtGyeonggi).isNotEmpty();
      assertThat(exhibitionAtGyeonggi.get())
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

    private User user2;

    @BeforeEach
    void setUp() {
      Role role = new Role(Authority.USER);
      em.persist(role);

      User user1 = new User("test@example.com", "Emily", List.of(role));
      em.persist(user1);

      user2 = new User("tes2t@example.com", "Jerry", List.of(role));
      em.persist(user2);

      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.now().plusDays(10))
          .endDate(LocalDate.now().plusDays(15))
          .genre(INSATALLATION)
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

      Exhibition exhibitionAlreadyEnd = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(6))
          .endDate(LocalDate.now().minusDays(3))
          .genre(INSATALLATION)
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

      em.persist(new ExhibitionLike(user1, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user1, exhibitionAlreadyEnd));
      em.persist(new ExhibitionLike(user2, exhibitionAlreadyEnd));

      em.flush();
      em.clear();
    }


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

      assertThat(exhibitionsPagingResult.getContent()).hasSize(1);
      assertThat(exhibitionsPagingResult.getContent().get(0))
          .hasFieldOrPropertyWithValue("isLiked", false);
    }
  }

  @Nested
  @DisplayName("findExhibitionsForReview() 테스트")
  class FindExhibitionsForReviewTest {

    @BeforeEach
    void setUp() {
      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.now().minusDays(5))
          .endDate(LocalDate.now().plusDays(15))
          .genre(INSATALLATION)
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

      Exhibition exhibitionGyeonggi = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().plusDays(20))
          .endDate(LocalDate.now().plusDays(30))
          .genre(INSATALLATION)
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
      em.persist(exhibitionGyeonggi);

      em.flush();
      em.clear();
    }


    @Test
    @DisplayName("검색어에 맞는 결과가 반환되는지 테스트")
    void testFindExhibitionsForReview() {
      List<ExhibitionBasicForSimpleQuery> exhibitions = exhibitionRepository.findExhibitionsForReview(
          "전시");

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

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
      Role role = new Role(Authority.USER);
      em.persist(role);

      user1 = new User("test@example.com", "Emily", List.of(role));
      em.persist(user1);

      user2 = new User("tes2t@example.com", "Jerry", List.of(role));
      em.persist(user2);

      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.now().plusDays(10))
          .endDate(LocalDate.now().plusDays(15))
          .genre(INSATALLATION)
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

      Exhibition exhibitionAlreadyEnd = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(6))
          .endDate(LocalDate.now().minusDays(3))
          .genre(INSATALLATION)
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

      em.persist(new ExhibitionLike(user1, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user1, exhibitionAlreadyEnd));
      em.persist(new ExhibitionLike(user2, exhibitionAlreadyEnd));

      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("로그인 유저와 조회 대상인 유저가 일치하지 않는 경우")
    void testDifferentUser() {
      // user2 : 로그인 유저
      // user1 : 조회 대상
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findUserLikeExhibitions(user2.getId(), user1.getId(), PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(2);

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
      assertThat(contents).hasSize(2);

      for (ExhibitionBasicForSimpleQuery exhibition : contents) {
        assertThat(exhibition).hasFieldOrPropertyWithValue("isLiked", true);
      }
    }
  }

  @Nested
  @DisplayName("findExhibitionsByCustomCondition() 테스트")
  class FindExhibitionsByCustomConditionTest {

    private User user1;

    @BeforeEach
    void setUp() {
      Role role = new Role(Authority.USER);
      em.persist(role);

      user1 = new User("test@example.com", "Emily", List.of(role));
      em.persist(user1);

      User user2 = new User("tes2t@example.com", "Jerry", List.of(role));
      em.persist(user2);

      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.of(LocalDate.now().getYear() - 1, 12, 21))
          .endDate(LocalDate.of(LocalDate.now().getYear(), 3, 7))
          .genre(INSATALLATION)
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

      Exhibition exhibitionAtSeoul = Exhibition.builder()
          .seq(33)
          .name("전시회 at 서울")
          .startDate(LocalDate.of(LocalDate.now().getYear(), 6, 1))
          .endDate(LocalDate.of(LocalDate.now().getYear(), 8, 15))
          .genre(MEDIA)
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

      Exhibition exhibitionAtGyeonggi = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.of(LocalDate.now().getYear(), 12, 11))
          .endDate(LocalDate.of(LocalDate.now().getYear() + 1, 2, 10))
          .genre(PHOTO)
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
      em.persist(exhibitionAtGyeonggi);

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
      em.persist(new ExhibitionLike(user1, exhibitionAtGyeonggi));
      em.persist(new ExhibitionLike(user2, exhibitionAtGyeonggi));

      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("지역 조건 테스트")
    void testAreaCondition() {
      // 부산 경기
      Set<Area> areas = new HashSet<>(Arrays.asList(BUSAN, GYEONGGI));
      Set<Month> months = new HashSet<>();
      Set<Genre> genres = new HashSet<>();
      boolean includeEnd = true;

      ExhibitionCustomCondition exhibitionCustomCondition = ExhibitionCustomCondition.builder()
          .areas(areas)
          .months(months)
          .genres(genres)
          .includeEnd(includeEnd)
          .build();

      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findExhibitionsByCustomCondition(null, exhibitionCustomCondition, PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(2);

      assertThat(contents.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산");

      assertThat(contents.get(1))
          .hasFieldOrPropertyWithValue("name", "전시회 at 경기");
    }

    @Test
    @DisplayName("시기 조건 테스트")
    void testMonthCondition() {
      // 1, 12 -> 부산 경기
      Set<Area> areas = new HashSet<>();
      Set<Month> months = new HashSet<>(Arrays.asList(JAN, DEC));
      Set<Genre> genres = new HashSet<>();
      boolean includeEnd = true;

      ExhibitionCustomCondition exhibitionCustomCondition = ExhibitionCustomCondition.builder()
          .areas(areas)
          .months(months)
          .genres(genres)
          .includeEnd(includeEnd)
          .build();

      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findExhibitionsByCustomCondition(null, exhibitionCustomCondition, PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(2);

      assertThat(contents.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산");

      assertThat(contents.get(1))
          .hasFieldOrPropertyWithValue("name", "전시회 at 경기");
    }

    @Test
    @DisplayName("장르 조건 테스트")
    void testGenreCondition() {
      Set<Area> areas = new HashSet<>();
      Set<Month> months = new HashSet<>();
      Set<Genre> genres = new HashSet<>(Arrays.asList(MEDIA, SHOW));
      boolean includeEnd = true;

      ExhibitionCustomCondition exhibitionCustomCondition = ExhibitionCustomCondition.builder()
          .areas(areas)
          .months(months)
          .genres(genres)
          .includeEnd(includeEnd)
          .build();

      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findExhibitionsByCustomCondition(
          null, exhibitionCustomCondition, PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(1);

      assertThat(contents.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 서울");
    }

    @Test
    @DisplayName("로그인한 상태에서 복합 조건 테스트")
    void testComplexCondition() {
      // 로그인(user1)
      // 3 5 6
      // 부산 경기
      // includeEnd = true
      // => 부산

      Set<Area> areas = new HashSet<>(Arrays.asList(BUSAN, GYEONGGI));
      Set<Month> months = new HashSet<>(Arrays.asList(MAR, MAY, JUN));
      Set<Genre> genres = new HashSet<>(Arrays.asList(PHOTO, MEDIA, INSATALLATION));
      boolean includeEnd = true;

      ExhibitionCustomCondition exhibitionCustomCondition = ExhibitionCustomCondition.builder()
          .areas(areas)
          .months(months)
          .genres(genres)
          .includeEnd(includeEnd)
          .build();

      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
          .findExhibitionsByCustomCondition(user1.getId(), exhibitionCustomCondition,
              PageRequest.of(0, 8));

      List<ExhibitionForSimpleQuery> contents = exhibitionsPagingResult.getContent();
      assertThat(contents).hasSize(1);

      assertThat(contents.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산")
          .hasFieldOrPropertyWithValue("isLiked", true);
    }
  }

  @Nested
  @DisplayName("findExhibitionAroundMe() 테스트")
  class FindExhibitionAroundMeTest {

    @BeforeEach
    void setUp() {
      Exhibition exhibitionAtBusan = Exhibition.builder()
          .seq(32)
          .name("전시회 at 부산")
          .startDate(LocalDate.now().plusDays(10))
          .endDate(LocalDate.now().plusDays(15))
          .genre(INSATALLATION)
          .description("이것은 전시회 설명입니다.")
          .latitude(37.501086)
          .longitude(127.053447)
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

      Exhibition exhibitionAtSeoul = Exhibition.builder()
          .seq(33)
          .name("전시회 at 서울")
          .startDate(LocalDate.now().plusDays(20))
          .endDate(LocalDate.now().plusDays(25))
          .genre(INSATALLATION)
          .description("이것은 전시회 설명입니다.")
          .latitude(37.564138)
          .longitude(126.973763)
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

      Exhibition exhibitionAtGyeonggi = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(10))
          .endDate(LocalDate.now().minusDays(5))
          .genre(INSATALLATION)
          .description("이것은 전시회 설명입니다.")
          .latitude(37.496193)
          .longitude(127.030906)
          .area(GYEONGGI)
          .place("미술관")
          .address("경기도 성남시")
          .inquiry("문의처 정보")
          .fee("성인 20,000원")
          .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg")
          .url("https://www.example.com")
          .placeUrl("https://www.place-example.com")
          .build();
      em.persist(exhibitionAtGyeonggi);

      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("내 주변 전시회 3KM 조건 테스트")
    void testAroundMe3KM() {
      List<ExhibitionWithLocationForSimpleQuery> exhibitions = exhibitionRepository
          .findExhibitionsAroundMe(null, 37.492001, 127.029704, 3);

      assertThat(exhibitions).hasSize(1);
      assertThat(exhibitions.get(0))
          .hasFieldOrPropertyWithValue("name", "전시회 at 부산")
          .hasFieldOrPropertyWithValue("location.latitude", 37.501086)
          .hasFieldOrPropertyWithValue("location.longitude", 127.053447);
    }
  }

  @Nested
  @DisplayName("findExhibitionForReview() 테스트")
  class FindExhibitionForReviewTest {

    private User user1;
    private User user2;

    private Exhibition exhibitionAtBusan;
    private Exhibition exhibitionAtSeoul;

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
          .startDate(LocalDate.of(LocalDate.now().getYear() - 1, 12, 21))
          .endDate(LocalDate.of(LocalDate.now().getYear(), 3, 7))
          .genre(INSATALLATION)
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
          .startDate(LocalDate.of(LocalDate.now().getYear(), 6, 1))
          .endDate(LocalDate.of(LocalDate.now().getYear(), 8, 15))
          .genre(INSATALLATION)
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

      Review review1 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      em.persist(review1);

      Review review2 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      em.persist(review2);

      Review review3 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(false)
          .build();
      em.persist(review3);

      Review review4 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtSeoul)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      review4.updateIdDeleted(true);
      em.persist(review4);

      em.persist(new ExhibitionLike(user1, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user2, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user1, exhibitionAtSeoul));

      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("userId == null인 경우 전시회 정보, 전시회 좋아요 여부, 전시회 좋아요 개수, 전시회 후기 개수 조회 테스트")
    void testFindExhibitionForReviewWhenUserIdNull() {
      Optional<ReviewExhibitionInfo> maybeBusanExhibition = exhibitionRepository.findExhibitionForReview(
          null, exhibitionAtBusan.getId());
      Optional<ReviewExhibitionInfo> maybeSeoulExhibition = exhibitionRepository.findExhibitionForReview(
          null, exhibitionAtSeoul.getId());

      assertThat(maybeBusanExhibition.isPresent()).isTrue();
      assertThat(maybeBusanExhibition.get().getExhibitionId()).isEqualTo(exhibitionAtBusan.getId());
      assertThat(maybeBusanExhibition.get().getName()).isEqualTo(exhibitionAtBusan.getName());
      assertThat(maybeBusanExhibition.get().getThumbnail()).isEqualTo(
          exhibitionAtBusan.getThumbnail());
      assertThat(maybeBusanExhibition.get().getStartDate()).isEqualTo(
          exhibitionAtBusan.getPeriod().getStartDate());
      assertThat(maybeBusanExhibition.get().getEndDate()).isEqualTo(
          exhibitionAtBusan.getPeriod().getEndDate());
      assertThat(maybeBusanExhibition.get().getIsLiked()).isFalse();
      assertThat(maybeBusanExhibition.get().getLikeCount()).isEqualTo(2);
      assertThat(maybeBusanExhibition.get().getReviewCount()).isEqualTo(2);

      assertThat(maybeSeoulExhibition.isPresent()).isTrue();
      assertThat(maybeSeoulExhibition.get().getExhibitionId()).isEqualTo(exhibitionAtSeoul.getId());
      assertThat(maybeSeoulExhibition.get().getName()).isEqualTo(exhibitionAtSeoul.getName());
      assertThat(maybeSeoulExhibition.get().getThumbnail()).isEqualTo(
          exhibitionAtSeoul.getThumbnail());
      assertThat(maybeSeoulExhibition.get().getStartDate()).isEqualTo(
          exhibitionAtSeoul.getPeriod().getStartDate());
      assertThat(maybeSeoulExhibition.get().getEndDate()).isEqualTo(
          exhibitionAtSeoul.getPeriod().getEndDate());
      assertThat(maybeSeoulExhibition.get().getIsLiked()).isFalse();
      assertThat(maybeSeoulExhibition.get().getLikeCount()).isEqualTo(1);
      assertThat(maybeSeoulExhibition.get().getReviewCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("userId != null인 경우 전시회 정보, 전시회 좋아요 여부, 전시회 좋아요 개수, 전시회 후기 개수 조회 테스트")
    void testFindExhibitionForReviewWhenUserIdNotNull() {
      Optional<ReviewExhibitionInfo> maybeBusanExhibition = exhibitionRepository.findExhibitionForReview(
          user1.getId(), exhibitionAtBusan.getId());
      Optional<ReviewExhibitionInfo> maybeSeoulExhibition = exhibitionRepository.findExhibitionForReview(
          user2.getId(), exhibitionAtSeoul.getId());

      assertThat(maybeBusanExhibition.isPresent()).isTrue();
      assertThat(maybeBusanExhibition.get().getExhibitionId()).isEqualTo(exhibitionAtBusan.getId());
      assertThat(maybeBusanExhibition.get().getName()).isEqualTo(exhibitionAtBusan.getName());
      assertThat(maybeBusanExhibition.get().getThumbnail()).isEqualTo(
          exhibitionAtBusan.getThumbnail());
      assertThat(maybeBusanExhibition.get().getStartDate()).isEqualTo(
          exhibitionAtBusan.getPeriod().getStartDate());
      assertThat(maybeBusanExhibition.get().getEndDate()).isEqualTo(
          exhibitionAtBusan.getPeriod().getEndDate());
      assertThat(maybeBusanExhibition.get().getIsLiked()).isTrue();
      assertThat(maybeBusanExhibition.get().getLikeCount()).isEqualTo(2);
      assertThat(maybeBusanExhibition.get().getReviewCount()).isEqualTo(2);

      assertThat(maybeSeoulExhibition.isPresent()).isTrue();
      assertThat(maybeSeoulExhibition.get().getExhibitionId()).isEqualTo(exhibitionAtSeoul.getId());
      assertThat(maybeSeoulExhibition.get().getName()).isEqualTo(exhibitionAtSeoul.getName());
      assertThat(maybeSeoulExhibition.get().getThumbnail()).isEqualTo(
          exhibitionAtSeoul.getThumbnail());
      assertThat(maybeSeoulExhibition.get().getStartDate()).isEqualTo(
          exhibitionAtSeoul.getPeriod().getStartDate());
      assertThat(maybeSeoulExhibition.get().getEndDate()).isEqualTo(
          exhibitionAtSeoul.getPeriod().getEndDate());
      assertThat(maybeSeoulExhibition.get().getIsLiked()).isFalse();
      assertThat(maybeSeoulExhibition.get().getLikeCount()).isEqualTo(1);
      assertThat(maybeSeoulExhibition.get().getReviewCount()).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("관리자 전시회 조회 테스트들")
  class FindExhibitionsByAdmin {

    private User user1;
    private User user2;

    private Exhibition exhibitionAtBusan;
    private Exhibition exhibitionAtSeoul;
    private Exhibition exhibitionAtGyeonggi;

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
          .startDate(LocalDate.of(LocalDate.now().getYear() - 1, 12, 21))
          .endDate(LocalDate.of(LocalDate.now().getYear(), 3, 7))
          .genre(INSATALLATION)
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
          .startDate(LocalDate.of(LocalDate.now().getYear(), 6, 1))
          .endDate(LocalDate.of(LocalDate.now().getYear(), 8, 15))
          .genre(INSATALLATION)
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

      exhibitionAtGyeonggi = Exhibition.builder()
          .seq(34)
          .name("전시회 at 경기")
          .startDate(LocalDate.now().minusDays(10))
          .endDate(LocalDate.now().minusDays(5))
          .genre(INSATALLATION)
          .description("이것은 전시회 설명입니다.")
          .latitude(37.496193)
          .longitude(127.030906)
          .area(GYEONGGI)
          .place("미술관")
          .address("경기도 성남시")
          .inquiry("문의처 정보")
          .fee("성인 20,000원")
          .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022072010193392447.jpg")
          .url("https://www.example.com")
          .placeUrl("https://www.place-example.com")
          .build();
      em.persist(exhibitionAtGyeonggi);

      Review review1 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      em.persist(review1);

      Review review2 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      em.persist(review2);

      Review review3 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtBusan)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(false)
          .build();
      em.persist(review3);

      Review review4 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtSeoul)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      review4.updateIdDeleted(true);
      em.persist(review4);

      Review review5 = Review.builder()
          .user(user1)
          .exhibition(exhibitionAtGyeonggi)
          .content("이것은 리뷰 본문입니다.")
          .title("이것은 리뷰 제목입니다.")
          .date(LocalDate.now())
          .isPublic(true)
          .build();
      em.persist(review5);

      em.persist(new ExhibitionLike(user1, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user2, exhibitionAtBusan));
      em.persist(new ExhibitionLike(user1, exhibitionAtSeoul));

      em.flush();
      em.clear();
    }

    @Test
    @DisplayName("[관리자] - 전시회 다건 조회 테스트 (생성일자 순 정렬)")
    void testFindExhibitionsByAdminOrderByCreatedAt() {
      // given
      Pageable pageable = PageRequest.of(
          0, 10, Sort.by(Direction.DESC, "createdAt")
      );

      //when
      Page<ExhibitionForSimpleQuery> response = exhibitionRepository.findExhibitionsByAdmin(pageable);

      //then
      assertThat(response.getContent()).hasSize(3);
      assertThat(response.getContent().get(0))
          .hasFieldOrPropertyWithValue("id", exhibitionAtGyeonggi.getId())
          .hasFieldOrPropertyWithValue("name", exhibitionAtGyeonggi.getName())
          .hasFieldOrPropertyWithValue("thumbnail", exhibitionAtGyeonggi.getThumbnail());
      assertThat(response.getContent().get(0).getPeriod())
          .hasFieldOrPropertyWithValue("startDate", exhibitionAtGyeonggi.getPeriod().getStartDate())
          .hasFieldOrPropertyWithValue("endDate", exhibitionAtGyeonggi.getPeriod().getEndDate());
    }

    @Test
    @DisplayName("[관리자] - 전시회 다건 조회 테스트 (좋아요 수 순 정렬)")
    void testFindExhibitionsByAdminOrderByLikeCount() {
      // given
      Pageable pageable = PageRequest.of(
          0, 10, Sort.by(Direction.DESC, "likeCount")
      );

      //when
      Page<ExhibitionForSimpleQuery> response = exhibitionRepository.findExhibitionsByAdmin(pageable);

      //then
      assertThat(response.getContent()).hasSize(3);
      assertThat(response.getContent().get(0))
          .hasFieldOrPropertyWithValue("id", exhibitionAtBusan.getId())
          .hasFieldOrPropertyWithValue("name", exhibitionAtBusan.getName())
          .hasFieldOrPropertyWithValue("thumbnail", exhibitionAtBusan.getThumbnail());
      assertThat(response.getContent().get(0).getPeriod())
          .hasFieldOrPropertyWithValue("startDate", exhibitionAtBusan.getPeriod().getStartDate())
          .hasFieldOrPropertyWithValue("endDate", exhibitionAtBusan.getPeriod().getEndDate());
    }

    @Test
    @DisplayName("[관리자] - 전시회 다건 조회 테스트 (잘못된 정렬 기준)")
    void testFindExhibitionsByAdminOrderByInvalidSort() {
      // given
      Pageable pageable = PageRequest.of(
          0, 10, Sort.by(Direction.DESC, "something wrong")
      );

      //when //then
      assertThatThrownBy(() -> {
        exhibitionRepository.findExhibitionsByAdmin(pageable);
      }).isInstanceOf(NotFoundException.class)
          .hasMessage(ErrorCode.INVALID_EXHB_SORT_TYPE.getMessage());
    }
  }
}