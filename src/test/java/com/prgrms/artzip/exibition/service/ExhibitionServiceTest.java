package com.prgrms.artzip.exibition.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.exibition.domain.Period;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.ExhibitionForSimpleQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ExhibitionServiceTest {
  @Mock
  ExhibitionRepository exhibitionRepository;

  @InjectMocks
  private ExhibitionService exhibitionService;

  private PageRequest pageRequest;

  @Test
  @DisplayName("다가오는 전시회 조회 테스트")
  void testGetUpcomingExhibitions() {
    pageRequest = PageRequest.of(0, 1);
    List<ExhibitionForSimpleQuery> exhibitions = new ArrayList<>();
    exhibitions.add(ExhibitionForSimpleQuery.builder()
            .exhibitionId(11L)
            .name("요리조리 MOKA Garden")
            .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
            .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
            .likeCount(30)
            .reviewCount(15)
        .build());
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

    // given
    when(exhibitionRepository.findUpcomingExhibition(pageRequest)).thenReturn(exhibitionsPagingResult);

    // when
    exhibitionService.getUpcomingExhibitions(pageRequest);

    // then
    verify(exhibitionRepository).findUpcomingExhibition(pageRequest);
  }
}