package com.prgrms.artzip.exibition.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exibition.domain.Area;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.Genre;
import com.prgrms.artzip.exibition.dto.ExhibitionForSimpleQuery;
import com.prgrms.artzip.review.domain.Review;
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

@DataJpaTest
@Import({QueryDslTestConfig.class})
class ExhibitionRepositoryTest {
  @PersistenceContext
  private EntityManager em;

  @Autowired
  private ExhibitionRepository exhibitionRepository;

  @BeforeEach
  void setUp() {
    Role role = new Role(Authority.USER);
    em.persist(role);

    User user = new User("test@example.com", "Emily", List.of(role));
    em.persist(user);

    Exhibition exhibitionAtBusan = Exhibition.builder()
        .seq(32)
        .name("전시회 at 부산")
        .startDate(LocalDate.of(2022, 12, 1))
        .endDate(LocalDate.of(2022, 12, 30))
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

    Exhibition exhibitionAtSeoul = Exhibition.builder()
        .seq(33)
        .name("전시회 at 서울")
        .startDate(LocalDate.of(2022, 11, 11))
        .endDate(LocalDate.of(2022, 12, 30))
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

    Review review = Review.builder()
        .user(user)
        .exhibition(exhibitionAtBusan)
        .content("이것은 리뷰 본문입니다.")
        .title("이것은 리뷰 제목입니다.")
        .date(LocalDate.now())
        .isPublic(true)
        .build();
    em.persist(review);

    ExhibitionLike exhibitionLike = new ExhibitionLike(exhibitionAtBusan, user);
    em.persist(exhibitionLike);

    em.flush();
    em.clear();
  }

  @Test
  @DisplayName("실제로 시작일이 빠른 전시회가 먼저 오는지 확인하는 테스트")
  void testFindUpcomingExhibition() {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUpcomingExhibition(LocalDate.of(2022, 7, 30), PageRequest.of(0, 10));

    ExhibitionForSimpleQuery exhibitionAtSeoul = exhibitionsPagingResult.getContent().get(0);

    assertThat(exhibitionAtSeoul.getName()).isEqualTo("전시회 at 서울");
    assertThat(exhibitionAtSeoul.getLikeCount()).isEqualTo(0);
    assertThat(exhibitionAtSeoul.getReviewCount()).isEqualTo(0);
  }

}