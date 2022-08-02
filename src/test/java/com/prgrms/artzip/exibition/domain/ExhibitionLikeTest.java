package com.prgrms.artzip.exibition.domain;

import static com.prgrms.artzip.exibition.domain.enumType.Area.GYEONGGI;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("ExhibitionLike 엔티티 테스트")
class ExhibitionLikeTest {

  private Exhibition exhibition = Exhibition.builder()
      .seq(12345)
      .name("전시회 제목")
      .startDate(LocalDate.now())
      .endDate(LocalDate.now())
      .latitude(123.321)
      .longitude(123.123)
      .area(GYEONGGI)
      .place("전시관")
      .address("경기도 용인시 수지구")
      .inquiry("010-0000-0000")
      .fee("1,000원")
      .thumbnail("http://image.com")
      .build();

  private User user = new User("test@test.com", "tester", List.of(new Role(Authority.USER)));

  @Test
  @DisplayName("전시회 정보가 null인 경우 테스트")
  void testExhibitionNull() {
    assertThatThrownBy(() -> new ExhibitionLike(null, user))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("전시회 좋아요에는 전시회 정보와 사용자 정보가 필수입니다.");
  }

  @Test
  @DisplayName("사용자 정보가 null인 경우 테스트")
  void testUserNull() {
    assertThatThrownBy(() -> new ExhibitionLike(exhibition, null))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage("전시회 좋아요에는 전시회 정보와 사용자 정보가 필수입니다.");
  }
}