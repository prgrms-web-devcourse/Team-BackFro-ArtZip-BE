package com.prgrms.artzip.exibition.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
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

  private Exhibition exhibitionAtBusan;
  private Exhibition exhibitionAtSeoul;
  private Exhibition exhibitionAlreadyEnd;

  @BeforeEach
  void setUp() {
    Role role = new Role(Authority.USER);
    em.persist(role);

    User user1 = new User("test@example.com", "Emily", List.of(role));
    em.persist(user1);

    User user2 = new User("tes2t@example.com", "Jerry", List.of(role));
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

    em.persist(new ExhibitionLike(exhibitionAtBusan, user1));
    em.persist(new ExhibitionLike(exhibitionAlreadyEnd, user1));
    em.persist(new ExhibitionLike(exhibitionAlreadyEnd, user2));

    em.flush();
    em.clear();
  }

  @Nested
  @DisplayName("findUpcomingExhibitions() 테스트")
  class FindUpcomingExhibitionsTest {

    @Test
    @DisplayName("실제로 시작일이 빠른 전시회가 먼저 오는지 확인하는 테스트")
    void testFindUpcomingExhibition() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUpcomingExhibitions(
          PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtSeoul = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionsPagingResult.getContent().size()).isEqualTo(2);
      assertThat(exhibitionAtSeoul.getName()).isEqualTo("전시회 at 서울");
      assertThat(exhibitionAtSeoul.getLikeCount()).isEqualTo(0);
      assertThat(exhibitionAtSeoul.getReviewCount()).isEqualTo(0);
    }
  }

  @Nested
  @DisplayName("findMostLikeExhibitions() 테스트")
  class FindMostLikeExhibitionsTest {

    @Test
    @DisplayName("종료된 전시회 포함하여 인기 많은 전시회 조회 테스트")
    void testFindMostLikeExhibitionIncludeEnd() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findMostLikeExhibitions(
          true, PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtSeoul = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionsPagingResult.getContent().size()).isEqualTo(3);
      assertThat(exhibitionAtSeoul.getName()).isEqualTo("전시회 at 경기");
      assertThat(exhibitionAtSeoul.getLikeCount()).isEqualTo(2);
      assertThat(exhibitionAtSeoul.getReviewCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("종료된 전시회 제외하고 인기 많은 전시회 조회 테스트")
    void testFindMostLikeExhibitionExcludeEnd() {
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findMostLikeExhibitions(
          false, PageRequest.of(0, 10));
      ExhibitionForSimpleQuery exhibitionAtSeoul = exhibitionsPagingResult.getContent().get(0);

      assertThat(exhibitionsPagingResult.getContent().size()).isEqualTo(2);
      assertThat(exhibitionAtSeoul.getName()).isEqualTo("전시회 at 부산");
      assertThat(exhibitionAtSeoul.getLikeCount()).isEqualTo(1);
      assertThat(exhibitionAtSeoul.getReviewCount()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("findExhibition() 테스트")
  class FindExhibitionTest {

    @Test
    @DisplayName("존재하지 않는 전시회 조회 테스트")
    void testFindEmptyExhibition() {
      Optional<ExhibitionDetailForSimpleQuery> exhibition = exhibitionRepository.findExhibition(
          123431L);
      assertThat(exhibition).isEmpty();
    }

    @Test
    @DisplayName("전시회 조회 테스트")
    void testFindExhibition() {
      Optional<ExhibitionDetailForSimpleQuery> exhibition = exhibitionRepository.findExhibition(
          exhibitionAlreadyEnd.getId());

      assertThat(exhibition).isNotEmpty();
      assertThat(exhibition.get().getSeq()).isEqualTo(34);
      assertThat(exhibition.get().getName()).isEqualTo("전시회 at 경기");
      assertThat(exhibition.get().getInquiry()).isEqualTo("문의처 정보");
      assertThat(exhibition.get().getUrl()).isEqualTo("https://www.example.com");
      assertThat(exhibition.get().getPlaceUrl()).isEqualTo("https://www.place-example.com");
      assertThat(exhibition.get().getLikeCount()).isEqualTo(2);
    }
  }
}