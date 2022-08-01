package com.prgrms.artzip.exibition.domain;

import static com.prgrms.artzip.exibition.domain.enumType.Area.GYEONGGI;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Exhibition 엔티티 테스트")
class ExhibitionTest {

  @DisplayName("url 검증 테스트")
  @ParameterizedTest
  @MethodSource("thumbnailParameter")
  void testUrlValidation(String url) {
    Exhibition exhibition = Exhibition.builder()
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
        .thumbnail(url)
        .build();

    assertThat(exhibition.getThumbnail()).isEqualTo(url);
  }

  private static Stream<Arguments> thumbnailParameter() {
    return Stream.of(
        Arguments.of("https://www.example-thumbnail-image.png"),
        Arguments.of("https://www.example-thumbnail-image.jpg"),
        Arguments.of("https://www.example-thumbnail-image.jpeg"),
        Arguments.of("http://www.culture.go.kr/upload/rdf/21/11/show_20211181717993881.jpg"),
        Arguments.of("http://www.culture.go.kr/upload/rdf/22/01/show_2022011310122915168.png"),
        Arguments.of("http://soma.kspo.or.kr"),
        Arguments.of(
            "https://www.hangeul.go.kr/traceHangeul/traceHangeul1List.do?curr_menu_cd=0103010100"),
        Arguments.of(
            "https://www.hangeul.go.kr/traceHangeul/traceHangeul1List.do?curr_menu_cd=0103010100"),
        Arguments.of("http://galleryraon.com/?page_id=2472#upcoming")
    );
  }
}