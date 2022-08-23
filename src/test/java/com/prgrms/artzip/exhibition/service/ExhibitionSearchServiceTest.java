package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.domain.vo.Period;
import com.prgrms.artzip.exhibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCustomConditionRequest;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByQuery(null, query, true, PageRequest.of(0, 10)))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_QUERY.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByQuery(null, query, true, PageRequest.of(0, 10));
        }

        @Test
        @DisplayName("성공적으로 검색한 경우 테스트")
        void testNormalQuery() {
            PageRequest pageRequest = PageRequest.of(0, 10);
            Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(List.of(ExhibitionForSimpleQuery.builder()
                    .id(11L)
                    .name("고흐 전시")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
                    .isLiked(false)
                    .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
                    .likeCount(30)
                    .reviewCount(15)
                    .build()));

            when(exhibitionRepository.findExhibitionsByQuery(null, "고흐", true, pageRequest)).thenReturn(exhibitionsPagingResult);

            exhibitionSearchService.getExhibitionsByQuery(null, "고흐", true, pageRequest);

            verify(exhibitionRepository).findExhibitionsByQuery(null, "고흐", true, pageRequest);
        }

        private static Stream<Arguments> queryParameter() {
            return Stream.of(
                    null,
                    Arguments.of(""),
                    Arguments.of("    "),
                    Arguments.of("고")
            );
        }
    }

    @Nested
    @DisplayName("getExhibitionsForReview() 테스트")
    class GetExhibitionsForReview {

        @ParameterizedTest
        @MethodSource("queryParameter")
        @DisplayName("검색어가 blank인 경우 테스트")
        void testBlankQuery(String query) {
            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsForReview(query))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_EXHB_QUERY_FOR_REVIEW.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsForReview(query);
        }

        @Test
        @DisplayName("성공적으로 검색한 경우 테스트")
        void testNormalQuery() {
            List<ExhibitionBasicForSimpleQuery> exhibitions = List.of(ExhibitionBasicForSimpleQuery.builder()
                    .id(11L)
                    .name("고흐 전시")
                    .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
                    .build());

            when(exhibitionRepository.findExhibitionsForReview("고흐")).thenReturn(exhibitions);

            exhibitionSearchService.getExhibitionsForReview("고흐");

            verify(exhibitionRepository).findExhibitionsForReview("고흐");
        }

        private static Stream<Arguments> queryParameter() {
            return Stream.of(null, Arguments.of(""), Arguments.of("    "));
        }
    }

    @Nested
    @DisplayName("getExhibitionsByCustomCondition() 테스트")
    class GetExhibitionsByCustomConditionTest {
        private Long userId = null;

        private boolean includeEnd = true;

        private Pageable pageable = PageRequest.of(0, 8);

        private Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(List.of(ExhibitionForSimpleQuery.builder()
                .id(11L)
                .name("고흐 전시")
                .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
                .isLiked(false)
                .period(new Period(LocalDate.of(LocalDate.now().getYear(), 6, 5), LocalDate.of(LocalDate.now().getYear(), 8, 10)))
                .likeCount(30)
                .reviewCount(15)
                .build()));

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 areas가 비어있는 경우 테스트")
        void testEmptyAreas() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(new ArrayList<>())
                    .months(List.of(Month.JUN))
                    .genres(List.of(Genre.INSTALLATION))
                    .build();

            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_INPUT_VALUE.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 months가 비어있는 경우 테스트")
        void testEmptyMonths() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(List.of(Area.SEOUL))
                    .months(new ArrayList<>())
                    .genres(List.of(Genre.INSTALLATION))
                    .build();

            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_INPUT_VALUE.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 genres가 비어있는 경우 테스트")
        void testEmptyGenres() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(List.of(Area.SEOUL))
                    .months(List.of(Month.JUN))
                    .genres(new ArrayList<>())
                    .build();

            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_INPUT_VALUE.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 areas에 null이 포함된 경우 테스트")
        void testAreasIncludeNull() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(Arrays.asList(Area.SEOUL, null))
                    .months(List.of(Month.JUN))
                    .genres(List.of(Genre.INSTALLATION))
                    .build();

            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_CUSTOM_EXHB_CONDITION.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 months에 null이 포함된 경우 테스트")
        void testMonthsIncludeNull() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(Arrays.asList(Area.SEOUL))
                    .months(Arrays.asList(Month.DEC, null))
                    .genres(Arrays.asList(Genre.INSTALLATION))
                    .build();

            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_CUSTOM_EXHB_CONDITION.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 genres에 null이 포함된 경우 테스트")
        void testGenresIncludeNull() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(Arrays.asList(Area.SEOUL))
                    .months(Arrays.asList(Month.DEC))
                    .genres(Arrays.asList(Genre.INSTALLATION, null))
                    .build();

            assertThatThrownBy(() -> exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable))
                    .isInstanceOf(InvalidRequestException.class)
                    .hasMessage(INVALID_CUSTOM_EXHB_CONDITION.getMessage());

            verify(exhibitionRepository, never()).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 areas에 ALL이 포함된 경우 테스트")
        void testAreasIncludeAll() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(Arrays.asList(Area.SEOUL, Area.ALL))
                    .months(Arrays.asList(Month.JUN))
                    .genres(Arrays.asList(Genre.INSTALLATION))
                    .build();

            when(exhibitionRepository.findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable))).thenReturn(exhibitionsPagingResult);

            exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable);

            verify(exhibitionRepository).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 months에 ALL이 포함된 경우 테스트")
        void testMonthsIncludeAll() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(Arrays.asList(Area.SEOUL))
                    .months(Arrays.asList(Month.JUN, Month.ALL))
                    .genres(Arrays.asList(Genre.INSTALLATION))
                    .build();

            when(exhibitionRepository.findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable))).thenReturn(exhibitionsPagingResult);

            exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable);

            verify(exhibitionRepository).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }

        @Test
        @DisplayName("ExhibitionCustomConditionRequest의 genres에 ALL이 포함된 경우 테스트")
        void testGenresIncludeAll() {
            ExhibitionCustomConditionRequest exhibitionCustomConditionRequest = ExhibitionCustomConditionRequest.builder()
                    .areas(Arrays.asList(Area.SEOUL))
                    .months(Arrays.asList(Month.JUN))
                    .genres(Arrays.asList(Genre.ALL))
                    .build();

            when(exhibitionRepository.findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable))).thenReturn(exhibitionsPagingResult);

            exhibitionSearchService.getExhibitionsByCustomCondition(userId, exhibitionCustomConditionRequest, includeEnd, pageable);

            verify(exhibitionRepository).findExhibitionsByCustomCondition(eq(userId), any(ExhibitionCustomCondition.class), eq(pageable));
        }
    }
}