package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionLikeRepository;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.response.ExhibitionLikeResponse;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExhibitionLikeService 테스트")
class ExhibitionLikeServiceTest {

  @Mock
  private ExhibitionRepository exhibitionRepository;

  @Mock
  private ExhibitionLikeRepository exhibitionLikeRepository;

  @InjectMocks
  private ExhibitionLikeService exhibitionLikeService;

  private Long userId = 2L;
  private User user = new User("test@example.com", "Emily", List.of(new Role(Authority.USER)));

  private Long exhibitionId = 1L;
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

  @Test
  @DisplayName("좋아요 추가시 전시회가 없는 경우 테스트")
  void testAddLikeExhibitionNotFound() {
    when(exhibitionLikeRepository.findByUserIdAndExhibitionId(user.getId(), exhibitionId))
        .thenReturn(Optional.empty());
    when(exhibitionRepository.findById(exhibitionId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> exhibitionLikeService.updateExhibitionLike(user, exhibitionId))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage(EXHB_NOT_FOUND.getMessage());

    verify(exhibitionLikeRepository).findByUserIdAndExhibitionId(user.getId(), exhibitionId);
    verify(exhibitionRepository).findById(exhibitionId);

    verify(exhibitionLikeRepository, never()).delete(any());
    verify(exhibitionLikeRepository, never()).save(any());
    verify(exhibitionLikeRepository, never()).countByExhibitionId(exhibitionId);
  }

  @Test
  @DisplayName("좋아요 추가 테스트")
  void testAddLike() {
    when(exhibitionLikeRepository.findByUserIdAndExhibitionId(user.getId(), exhibitionId))
        .thenReturn(Optional.empty());
    when(exhibitionRepository.findById(exhibitionId))
        .thenReturn(Optional.of(exhibition));
    when(exhibitionLikeRepository.countByExhibitionId(exhibitionId))
        .thenReturn(100L);

    ExhibitionLikeResponse exhibitionLikeResponse = exhibitionLikeService
        .updateExhibitionLike(user, exhibitionId);

    assertThat(exhibitionLikeResponse.getIsLiked()).isTrue();

    verify(exhibitionLikeRepository).findByUserIdAndExhibitionId(user.getId(), exhibitionId);
    verify(exhibitionRepository).findById(exhibitionId);
    verify(exhibitionLikeRepository).save(any());
    verify(exhibitionLikeRepository).countByExhibitionId(exhibitionId);

    verify(exhibitionLikeRepository, never()).delete(any());
  }

  @Test
  @DisplayName("좋아요 삭제")
  void testRemoveLike() {
    ExhibitionLike exhibitionLike = new ExhibitionLike(user, exhibition);

    when(exhibitionLikeRepository.findByUserIdAndExhibitionId(user.getId(), exhibitionId))
        .thenReturn(Optional.of(exhibitionLike));
    when(exhibitionLikeRepository.countByExhibitionId(exhibitionId))
        .thenReturn(100L);

    ExhibitionLikeResponse exhibitionLikeResponse = exhibitionLikeService
        .updateExhibitionLike(user, exhibitionId);

    verify(exhibitionLikeRepository).findByUserIdAndExhibitionId(user.getId(), exhibitionId);
    verify(exhibitionLikeRepository).delete(exhibitionLike);
    verify(exhibitionLikeRepository).countByExhibitionId(exhibitionId);

    verify(exhibitionRepository, never()).findById(exhibitionId);
    verify(exhibitionLikeRepository, never()).save(any());

    assertThat(exhibitionLikeResponse.getIsLiked()).isFalse();
  }

  @Test
  @DisplayName("유저가 좋아요 누른 전시회 개수 반환")
  void testGetExhibitionLikeCountByUserId() {
    // given
    when(exhibitionLikeRepository.countByUserId(1L)).thenReturn(3L);
    // when
    Long exhibitionLikeCount = exhibitionLikeService.getExhibitionLikeCountByUserId(1L);
    // then
    assertThat(exhibitionLikeCount).isEqualTo(3L);
    verify(exhibitionLikeRepository).countByUserId(1L);
  }
}