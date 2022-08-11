package com.prgrms.artzip.exhibition.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExhibitionAdminService 테스트")
class ExhibitionAdminServiceTest {

  @InjectMocks
  private ExhibitionAdminService exhibitionAdminService;

  @Mock
  private AmazonS3Uploader amazonS3Uploader;

  @Mock
  private ExhibitionRepository exhibitionRepository;

  private final Role role = new Role(Authority.USER);

  private final User user = LocalUser.builder()
      .email("test@test.com")
      .nickname("안녕하세요")
      .password("1q2w3e4r!")
      .roles(List.of(role))
      .build();

  private Exhibition exhibition = Exhibition.builder()
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
      .url("https://www.example.com")
      .placeUrl("https://www.place-example.com")
      .thumbnail("https://naver.com")
      .build();

  @Test
  @DisplayName("전시회 생성 테스트")
  void createExhibition() throws IOException {
    //given
    ExhibitionCreateRequest request = ExhibitionCreateRequest.builder()
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
        .url("https://www.example.com")
        .placeUrl("https://www.place-example.com")
        .build();
    MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE, "test1".getBytes());
    doReturn("https://naver.com").when(amazonS3Uploader).upload(file, "exhibition");
    doReturn(exhibition).when(exhibitionRepository).save(Mockito.any(Exhibition.class));

    //when
    exhibitionAdminService.createExhibition(request, file);

    //then
    verify(amazonS3Uploader).upload(file, "exhibition");
    verify(exhibitionRepository).save(Mockito.any(Exhibition.class));
  }
}