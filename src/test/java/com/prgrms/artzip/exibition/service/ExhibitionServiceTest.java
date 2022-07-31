package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.exibition.domain.Area.GYEONGGI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Location;
import com.prgrms.artzip.exibition.domain.Period;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExhibitionService 테스트")
class ExhibitionServiceTest {
  @Mock
  private ExhibitionRepository exhibitionRepository;

  @Mock
  private ExhibitionLikeService exhibitionLikeService;

  @InjectMocks
  private ExhibitionService exhibitionService;

  private PageRequest pageRequest;

  @Test
  @DisplayName("다가오는 전시회 조회 테스트")
  void testGetUpcomingExhibitions() {
    pageRequest = PageRequest.of(0, 1);
    List<ExhibitionForSimpleQuery> exhibitions = new ArrayList<>();
    exhibitions.add(ExhibitionForSimpleQuery.builder()
            .id(11L)
            .name("요리조리 MOKA Garden")
            .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
            .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
            .likeCount(30)
            .reviewCount(15)
        .build());
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

    // given
    when(exhibitionRepository.findUpcomingExhibitions(pageRequest)).thenReturn(exhibitionsPagingResult);

    // when
    exhibitionService.getUpcomingExhibitions(pageRequest);

    // then
    verify(exhibitionRepository).findUpcomingExhibitions(pageRequest);
  }

  @Test
  @DisplayName("인기 많은 전시회 조회 테스트")
  void testGetMostLikeExhibitions() {
    pageRequest = PageRequest.of(0, 1);
    List<ExhibitionForSimpleQuery> exhibitions = new ArrayList<>();
    exhibitions.add(ExhibitionForSimpleQuery.builder()
        .id(11L)
        .name("요리조리 MOKA Garden")
        .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
        .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
        .likeCount(30)
        .reviewCount(15)
        .build());
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

    // given
    when(exhibitionRepository.findMostLikeExhibitions(true, pageRequest)).thenReturn(exhibitionsPagingResult);

    // when
    exhibitionService.getMostLikeExhibitions(true, pageRequest);

    // then
    verify(exhibitionRepository).findMostLikeExhibitions(true, pageRequest);
  }

  // 임시로 작성. 수정 필요!
  @Nested
  @DisplayName("getExhibition() 테스트")
  class GetExhibitionTest {
    private User user = new User("test@example.com", "Emily", List.of(new Role(Authority.USER)));

    private Long exhibitionId = 123L;

    private ExhibitionDetailForSimpleQuery exhibition1 = ExhibitionDetailForSimpleQuery.builder()
        .id(exhibitionId)
        .seq(12345)
        .name("전시회 제목")
        .period(new Period(LocalDate.now(), LocalDate.now().plusDays(1)))
        .location(
            Location.builder()
                .latitude(123.321)
                .longitude(123.123)
                .area(GYEONGGI)
                .place("전시관")
                .address("경기도 용인시 수지구")
                .build()
        )
        .inquiry("010-0000-0000")
        .fee("1,000원")
        .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
        .url("http://soma.kspo.or.kr")
        .placeUrl("http://galleryraon.com")
        .likeCount(10)
        .build();

    private ExhibitionDetailForSimpleQuery exhibition2 = ExhibitionDetailForSimpleQuery.builder()
        .id(exhibitionId)
        .seq(12345)
        .name("전시회 제목2")
        .period(new Period(LocalDate.now(), LocalDate.now().plusDays(1)))
        .location(
            Location.builder()
                .latitude(123.321)
                .longitude(123.123)
                .area(GYEONGGI)
                .place("전시관")
                .address("경기도 용인시 수지구")
                .build()
        )
        .inquiry("010-0000-0000")
        .fee("1,000원")
        .thumbnail("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg")
        .likeCount(5)
        .build();

    @Test
    @DisplayName("존재하지 않는 게시물인 경우")
    void testExhibitionNotFound() {
      when(exhibitionRepository.findExhibition(exhibitionId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> exhibitionService.getExhibition(null, exhibitionId))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage("존재하지 않는 전시회 입니다.");
    }

    @Test
    @DisplayName("인증되지 않은 사용자인 경우")
    void testNotAuthorized() {
       when(exhibitionRepository.findExhibition(123L)).thenReturn(Optional.of(exhibition1));

      exhibitionService.getExhibition(null, exhibitionId);

       verify(exhibitionLikeService, never()).isLikedExhibition(any(), any());
    }

    @Test
    @DisplayName("인증된 사용자이며 좋아요를 누른 경우")
    void testAuthorizedLike() {
      when(exhibitionRepository.findExhibition(exhibitionId)).thenReturn(Optional.of(exhibition1));
      when(exhibitionLikeService.isLikedExhibition(exhibitionId, user.getId())).thenReturn(true);

      exhibitionService.getExhibition(user, exhibitionId);

      verify(exhibitionLikeService).isLikedExhibition(exhibitionId, user.getId());
    }

    @Test
    @DisplayName("인증된 사용자이며 좋아요를 누르지 않은 경우")
    void testAuthorizedNotLike() {
      when(exhibitionRepository.findExhibition(exhibitionId)).thenReturn(Optional.of(exhibition2));
      when(exhibitionLikeService.isLikedExhibition(exhibitionId, user.getId())).thenReturn(false);

      exhibitionService.getExhibition(user, exhibitionId);

      verify(exhibitionLikeService).isLikedExhibition(exhibitionId, user.getId());
    }
  }
}