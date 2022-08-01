package com.prgrms.artzip.exibition.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Period;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ExhibitionSearchServiceTest {

  @Mock
  private ExhibitionRepository exhibitionRepository;

  @InjectMocks
  private ExhibitionSearchService exhibitionSearchService;

  @Nested
  @DisplayName("getExhibitionByQuery() 테스트")
  class GetExhibitionByQueryTest {

    @ParameterizedTest
    @MethodSource("queryParameter")
    @DisplayName("검색어가 blank인 경우 테스트")
    void testBlankQuery(String query) {
      assertThatThrownBy(
          () -> exhibitionSearchService.getExhibitionByQuery(query, true,
              PageRequest.of(0, 10)))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage("검색어(query)는 필수 입니다.");

      verify(exhibitionRepository, never()).findExhibitionsByQuery(query, true,
          PageRequest.of(0, 10));
    }

    @Test
    @DisplayName("성공적으로 검색한 경우 테스트")
    void testNormalQuery() {
      PageRequest pageRequest = PageRequest.of(0, 10);
      List<ExhibitionForSimpleQuery> exhibitions = new ArrayList<>();
      exhibitions.add(ExhibitionForSimpleQuery.builder()
          .id(11L)
          .name("고흐 전시")
          .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
          .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
          .likeCount(30)
          .reviewCount(15)
          .build());
      Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

      when(exhibitionRepository.findExhibitionsByQuery("고흐", true, pageRequest)).thenReturn(
          exhibitionsPagingResult);

      exhibitionSearchService.getExhibitionByQuery("고흐", true, pageRequest);

      verify(exhibitionRepository).findExhibitionsByQuery("고흐", true, pageRequest);
    }

    private static Stream<Arguments> queryParameter() {
      return Stream.of(
          null,
          Arguments.of(""),
          Arguments.of(" ")
      );
    }
  }
}