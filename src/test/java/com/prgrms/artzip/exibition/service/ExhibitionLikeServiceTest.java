package com.prgrms.artzip.exibition.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.exibition.domain.Area;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.Genre;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionLikeRepository;
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
class ExhibitionLikeServiceTest {
  @Mock
  private ExhibitionLikeRepository exhibitionLikeRepository;

  @InjectMocks
  private ExhibitionLikeService exhibitionLikeService;

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

  private ExhibitionLike exhibitionLike = new ExhibitionLike(exhibition, user);

  @Test
  @DisplayName("사용자가 전시회에 좋아요를 누른 경우")
  void isLiked() {
    when(exhibitionLikeRepository.findById(any())).thenReturn(Optional.of(exhibitionLike));
    assertThat(exhibitionLikeService.isLikedExhibition(exhibition.getId(), user.getId())).isTrue();
  }

  @Test
  @DisplayName("사용자가 전시회에 좋아요를 누르지 않은 경우")
  void isNotLiked() {
    when(exhibitionLikeRepository.findById(any())).thenReturn(Optional.empty());
    assertThat(exhibitionLikeService.isLikedExhibition(exhibition.getId(), user.getId())).isFalse();
  }
}