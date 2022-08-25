package com.prgrms.artzip.exhibition.repository;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.ExhibitionLike;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({QueryDslTestConfig.class})
@DisplayName("ExhibitionLikeRepository 테스트")
class ExhibitionLikeRepositoryTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ExhibitionLikeRepository exhibitionLikeRepository;

    private Exhibition exhibition;
    private ExhibitionLike exhibitionLikeOfUser1;
    private ExhibitionLike exhibitionLikeOfUser2;
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

        exhibition = Exhibition.builder()
                .seq(32)
                .name("전시회 at 부산")
                .startDate(LocalDate.now().plusDays(10))
                .endDate(LocalDate.now().plusDays(15))
                .genre(Genre.MEDIA)
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
        em.persist(exhibition);

        exhibitionLikeOfUser1 = new ExhibitionLike(user1, exhibition);
        em.persist(exhibitionLikeOfUser1);
        exhibitionLikeOfUser2 = new ExhibitionLike(user2, exhibition);
        em.persist(exhibitionLikeOfUser2);

        em.flush();
        em.clear();
    }

    @Nested
    @DisplayName("countByExhibitionId() 테스트")
    class CountByExhibitionIdTest {
        @Test
        @DisplayName("전시회에 좋아요가 없는 경우 테스트")
        void testExhibitionWithoutLike() {
            Long countOfLike = exhibitionLikeRepository.countByExhibitionId(123L);
            assertThat(countOfLike).isEqualTo(0);
        }

        @Test
        @DisplayName("전시회 좋아요 개수 확인 테스트")
        void testExhibitionLikeCount() {
            Long countOfLike = exhibitionLikeRepository.countByExhibitionId(exhibition.getId());
            assertThat(countOfLike).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByExhibitionIdAndUserId() 테스트")
    class FindByExhibitionIdAndUserIdTest {
        @Test
        @DisplayName("좋아요가 존재하지 않는 경우 테스트")
        void testLikeNotExist() {
            Optional<ExhibitionLike> exhibitionLike = exhibitionLikeRepository.findByUserIdAndExhibitionId(123L, 123L);
            assertThat(exhibitionLike).isEmpty();
        }

        @Test
        @DisplayName("좋아요가 존재하는 경우 테스트")
        void testLikeExist() {
            Optional<ExhibitionLike> exhibitionLike = exhibitionLikeRepository.findByUserIdAndExhibitionId(user1.getId(), exhibition.getId());
            assertThat(exhibitionLike).isNotEmpty();
        }
    }
}