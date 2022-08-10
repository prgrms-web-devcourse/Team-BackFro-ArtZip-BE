package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;
import static com.prgrms.artzip.common.ErrorCode.INVALID_COORDINATE;
import static com.prgrms.artzip.common.ErrorCode.INVALID_DISTANCE;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.GYEONGGI;
import static com.prgrms.artzip.exhibition.domain.enumType.Area.SEOUL;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.domain.vo.Location;
import com.prgrms.artzip.exhibition.domain.vo.Period;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.dto.response.ReviewPhotoInfo;
import com.prgrms.artzip.review.dto.response.ReviewUserInfo;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import com.prgrms.artzip.review.service.ReviewService;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
  private ReviewService reviewService;

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
        .isLiked(false)
        .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
        .likeCount(30)
        .reviewCount(15)
        .build());
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

    // given
    when(exhibitionRepository.findUpcomingExhibitions(null, pageRequest))
        .thenReturn(exhibitionsPagingResult);

    // when
    exhibitionService.getUpcomingExhibitions(null, pageRequest);

    // then
    verify(exhibitionRepository).findUpcomingExhibitions(null, pageRequest);
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
        .isLiked(false)
        .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
        .likeCount(30)
        .reviewCount(15)
        .build());
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

    // given
    when(exhibitionRepository.findMostLikeExhibitions(null, true, pageRequest))
        .thenReturn(exhibitionsPagingResult);

    // when
    exhibitionService.getMostLikeExhibitions(null, true, pageRequest);

    // then
    verify(exhibitionRepository).findMostLikeExhibitions(null, true, pageRequest);
  }

  @Nested
  @DisplayName("getExhibition() 테스트")
  class GetExhibitionTest {

    private User user = new User("test@example.com", "Emily", List.of(new Role(Authority.USER)));

    private Exhibition exhibition = Exhibition.builder()
        .seq(32)
        .name("전시회 제목")
        .startDate(LocalDate.of(2022, 4, 11))
        .endDate(LocalDate.of(2022, 6, 2))
        .genre(Genre.FINEART)
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

    private Long userId = 321L;
    private Long exhibitionId = 123L;
    private ExhibitionDetailForSimpleQuery exhibitionDetail1 = ExhibitionDetailForSimpleQuery.builder()
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
        .isLiked(true)
        .likeCount(10)
        .build();

    private ExhibitionDetailForSimpleQuery exhibitionDetail2 = ExhibitionDetailForSimpleQuery.builder()
        .id(exhibitionId)
        .seq(12346)
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
        .isLiked(false)
        .likeCount(5)
        .build();

    Review review = new Review(user,
        exhibition,
        "리뷰 내용",
        "리뷰 제목",
        LocalDate.now(),
        true);

    List<ReviewsResponseForExhibitionDetail> reviews = Arrays.asList(
        ReviewsResponseForExhibitionDetail.builder()
            .reviewId(1L)
            .user(new ReviewUserInfo(user))
            .date(review.getDate())
            .title(review.getTitle())
            .content(review.getContent())
            .createdAt(review.getCreatedAt())
            .updatedAt(review.getUpdatedAt())
            .isEdited(false)
            .isPublic(review.getIsPublic())
            .isLiked(true)
            .likeCount(1L)
            .commentCount(0L)
            .photos(Arrays.asList(new ReviewPhotoInfo(
                new ReviewPhoto(review, "https://www.review-photo-path"))))
            .build());

    @Test
    @DisplayName("존재하지 않는 게시물인 경우")
    void testExhibitionNotFound() {
      when(exhibitionRepository.findExhibition(null, exhibitionId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> exhibitionService.getExhibition(null, exhibitionId))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage(EXHB_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("인증된 사용자이며 좋아요를 누른 경우")
    void testAuthorizedLike() {
      when(exhibitionRepository.findExhibition(userId, exhibitionId))
          .thenReturn(Optional.of(exhibitionDetail1));
      when(reviewService.getReviewsForExhibition(userId, exhibitionId))
          .thenReturn(reviews);

      exhibitionService.getExhibition(userId, exhibitionId);

      verify(exhibitionRepository).findExhibition(userId, exhibitionId);
      verify(reviewService).getReviewsForExhibition(userId, exhibitionId);
    }

    @Test
    @DisplayName("인증된 사용자이며 좋아요를 누르지 않은 경우")
    void testAuthorizedNotLike() {
      when(exhibitionRepository.findExhibition(null, exhibitionId))
          .thenReturn(Optional.of(exhibitionDetail2));
      when(reviewService.getReviewsForExhibition(null, exhibitionId))
          .thenReturn(reviews);

      exhibitionService.getExhibition(null, exhibitionId);

      verify(exhibitionRepository).findExhibition(null, exhibitionId);
      verify(reviewService).getReviewsForExhibition(null, exhibitionId);
    }
  }

  @Test
  @DisplayName("사용자가 좋아요 누른 전시회 조회 테스트")
  void testGetUserLikeExhibitions() {
    Long userId = 1L;
    Long exhibitionLikeUserId = 2L;
    pageRequest = PageRequest.of(0, 1);
    List<ExhibitionForSimpleQuery> exhibitions = new ArrayList<>();
    exhibitions.add(ExhibitionForSimpleQuery.builder()
        .id(11L)
        .name("요리조리 MOKA Garden")
        .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
        .isLiked(true)
        .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
        .likeCount(30)
        .reviewCount(15)
        .build());
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = new PageImpl(exhibitions);

    // given
    when(exhibitionRepository.findUserLikeExhibitions(userId, exhibitionLikeUserId, pageRequest))
        .thenReturn(exhibitionsPagingResult);

    // when
    exhibitionService.getUserLikeExhibitions(userId, exhibitionLikeUserId, pageRequest);

    // then
    verify(exhibitionRepository).findUserLikeExhibitions(userId, exhibitionLikeUserId, pageRequest);
  }


  @Nested
  @DisplayName("getExhibitionsAroundMe() 테스트")
  class GetExhibitionsAroundMeTest {

    @Test
    @DisplayName("위도 -100인 경우 테스트")
    void testBelowMinLatitude() {
      assertThatThrownBy(() -> exhibitionService.getExhibitionsAroundMe(null, -100, 128.12, 3))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage(INVALID_COORDINATE.getMessage());
    }

    @Test
    @DisplayName("위도 100인 경우 테스트")
    void testOverMaxLatitude() {
      assertThatThrownBy(() -> exhibitionService.getExhibitionsAroundMe(null, 100, 128.12, 3))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage(INVALID_COORDINATE.getMessage());
    }

    @Test
    @DisplayName("위도 -200인 경우 테스트")
    void testBelowMinLongitude() {
      assertThatThrownBy(() -> exhibitionService.getExhibitionsAroundMe(null, 35.12, -200, 3))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage(INVALID_COORDINATE.getMessage());
    }

    @Test
    @DisplayName("위도 200인 경우 테스트")
    void testOverMaxLongitude() {
      assertThatThrownBy(() -> exhibitionService.getExhibitionsAroundMe(null, 35.12, 200, 3))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage(INVALID_COORDINATE.getMessage());
    }

    @Test
    @DisplayName("거리 -5인 경우 테스트")
    void testWrongDistance() {
      assertThatThrownBy(() -> exhibitionService.getExhibitionsAroundMe(null, 35.12, 128.12, -5))
          .isInstanceOf(InvalidRequestException.class)
          .hasMessage(INVALID_DISTANCE.getMessage());
    }

    @Test
    @DisplayName("사용자 주변에 있는 전시회 조회 테스트")
    void testGetExhibitionsAroundMe() {
      List<ExhibitionWithLocationForSimpleQuery> exhibitions = Arrays.asList(
          ExhibitionWithLocationForSimpleQuery.builder()
              .id(11L)
              .name("요리조리 MOKA Garden")
              .thumbnail("http://www.culture.go.kr/upload/rdf/22/07/show_2022071411402126915.png")
              .isLiked(true)
              .period(new Period(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))
              .likeCount(30)
              .reviewCount(15)
              .location(new Location(30.12, 128.12, SEOUL, "서울 어딘가 전시관", "서울특별시 마포구"))
              .build()
      );

      when(exhibitionRepository.findExhibitionsAroundMe(null, 35.12, 128.12, 3))
          .thenReturn(exhibitions);

      exhibitionService.getExhibitionsAroundMe(null, 35.12, 128.12, 3);

      verify(exhibitionRepository).findExhibitionsAroundMe(null, 35.12, 128.12, 3);
    }
  }
}