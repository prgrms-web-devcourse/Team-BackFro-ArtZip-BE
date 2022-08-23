package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AWSException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Remover;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.domain.vo.Location;
import com.prgrms.artzip.exhibition.domain.vo.Period;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateOrUpdateRequest;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionSemiUpdateRequest;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExhibitionAdminService 테스트")
class ExhibitionAdminServiceTest {

    @InjectMocks
    private ExhibitionAdminService exhibitionAdminService;

    @Mock
    private AmazonS3Uploader amazonS3Uploader;

    @Mock
    private AmazonS3Remover amazonS3Remover;

    @Mock
    private ExhibitionRepository exhibitionRepository;

    private final String s3DirName = "exhibition";

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
            .genre(Genre.PAINTING)
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
    void testCreateExhibition() throws IOException {
        //given
        ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
                .name("전시회 제목")
                .startDate(LocalDate.of(2022, 4, 11))
                .endDate(LocalDate.of(2022, 6, 2))
                .genre(Genre.PAINTING)
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
        MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE,
                "test1".getBytes());
        doReturn("https://naver.com").when(amazonS3Uploader).upload(file, "exhibition");
        doReturn(exhibition).when(exhibitionRepository).save(Mockito.any(Exhibition.class));

        //when
        exhibitionAdminService.createExhibition(request, file);

        //then
        verify(amazonS3Uploader).upload(file, "exhibition");
        verify(exhibitionRepository).save(Mockito.any(Exhibition.class));
    }

    @Test
    @DisplayName("전시회 생성 테스트 (io exception)")
    void testCreateExhibitionWithIOException() throws IOException {
        //given
        ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
                .name("전시회 제목")
                .startDate(LocalDate.of(2022, 4, 11))
                .endDate(LocalDate.of(2022, 6, 2))
                .genre(Genre.PAINTING)
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
        MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE,
                "test1".getBytes());
        doThrow(IOException.class).when(amazonS3Uploader).upload(file, "exhibition");

        //when
        assertThatThrownBy(() -> {
            exhibitionAdminService.createExhibition(request, file);
        }).isInstanceOf(AWSException.class)
                .hasMessage(ErrorCode.AMAZON_S3_ERROR.getMessage());

        //then
        verify(amazonS3Uploader).upload(file, "exhibition");
    }

    @Test
    @DisplayName("전시회 다건 조회 테스트")
    void testGetExhibitions() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Direction.DESC, "createdAt"));
        ExhibitionForSimpleQuery sample = ExhibitionForSimpleQuery.builder()
                .id(0L)
                .name("전시회 제목")
                .thumbnail("https://naver.com")
                .isLiked(false)
                .period(new Period(LocalDate.of(2022, 4, 11), LocalDate.of(2022, 6, 2)))
                .likeCount(0L)
                .reviewCount(0L)
                .build();
        doReturn(new PageImpl(List.of(sample))).when(exhibitionRepository)
                .findExhibitionsByAdmin(pageable);

        //when
        exhibitionAdminService.getExhibitions(pageable);

        //then
        verify(exhibitionRepository).findExhibitionsByAdmin(pageable);
    }

    @Test
    @DisplayName("전시회 상세 조회 테스트")
    void testGetExhibition() {
        //given
        ExhibitionDetailForSimpleQuery sample = ExhibitionDetailForSimpleQuery.builder()
                .id(0L)
                .name("전시회 제목")
                .thumbnail("https://naver.com")
                .isLiked(false)
                .period(new Period(LocalDate.of(2022, 4, 11), LocalDate.of(2022, 6, 2)))
                .likeCount(0L)
                .location(Location.builder().latitude(38.5).longitude(37.5).build())
                .genre(Genre.PAINTING)
                .description("이것은 전시회 설명입니다.")
                .inquiry("문의처 정보")
                .fee("성인 20,000원")
                .url("https://www.example.com")
                .placeUrl("https://www.place-example.com")
                .build();
        doReturn(Optional.of(sample)).when(exhibitionRepository).findExhibition(null, 0L);

        //when
        exhibitionAdminService.getExhibitionDetail(0L);

        //then
        verify(exhibitionRepository).findExhibition(null, 0L);
    }

    @Test
    @DisplayName("전시회 상세 조회 테스트 (without url)")
    void testGetExhibitionWithoutUrl() {
        //given
        ExhibitionDetailForSimpleQuery sample = ExhibitionDetailForSimpleQuery.builder()
                .id(0L)
                .name("전시회 제목")
                .thumbnail("https://naver.com")
                .isLiked(false)
                .period(new Period(LocalDate.of(2022, 4, 11), LocalDate.of(2022, 6, 2)))
                .likeCount(0L)
                .location(Location.builder().latitude(38.5).longitude(37.5).build())
                .genre(Genre.PAINTING)
                .description("이것은 전시회 설명입니다.")
                .inquiry("문의처 정보")
                .fee("성인 20,000원")
                .build();
        doReturn(Optional.of(sample)).when(exhibitionRepository).findExhibition(null, 0L);

        //when
        exhibitionAdminService.getExhibitionDetail(0L);

        //then
        verify(exhibitionRepository).findExhibition(null, 0L);
    }

    @Test
    @DisplayName("유효하지 않은 전시회 상세 조회 테스트")
    void testGetInvalidExhibition() {
        //given
        doReturn(Optional.empty()).when(exhibitionRepository).findExhibition(null, 0L);

        //when
        assertThatThrownBy(() -> {
            exhibitionAdminService.getExhibitionDetail(0L);
        }).isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.EXHB_NOT_FOUND.getMessage());

        //then
        verify(exhibitionRepository).findExhibition(null, 0L);
    }

    @Test
    @DisplayName("전시회 업데이트 테스트")
    void testUpdateExhibition() throws IOException {
        // Given
        ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
                .name("전시회 제목")
                .startDate(LocalDate.of(2022, 4, 11))
                .endDate(LocalDate.of(2022, 6, 2))
                .genre(Genre.PAINTING)
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
        MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE,
                "test1".getBytes());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);
        doReturn("https://daum.net").when(amazonS3Uploader).upload(file, s3DirName);
        doNothing().when(amazonS3Remover).removeFile("https://naver.com", s3DirName);

        // when
        exhibitionAdminService.updateExhibition(0L, request, file);

        // then
        verify(exhibitionRepository).findById(0L);
        verify(amazonS3Uploader).upload(file, s3DirName);
        verify(amazonS3Remover).removeFile("https://naver.com", s3DirName);
    }

    @Test
    @DisplayName("전시회 업데이트 테스트 (썸네일 x)")
    void testUpdateExhibitionWithoutThumbnail() throws IOException {
        // Given
        ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
                .name("전시회 제목")
                .startDate(LocalDate.of(2022, 4, 11))
                .endDate(LocalDate.of(2022, 6, 2))
                .genre(Genre.PAINTING)
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
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);

        // when
        exhibitionAdminService.updateExhibition(0L, request, null);

        // then
        verify(exhibitionRepository).findById(0L);
    }

    @Test
    @DisplayName("전시회 업데이트 테스트 (IOException)")
    void testUpdateExhibitionWithIOException() throws IOException {
        // Given
        ExhibitionCreateOrUpdateRequest request = ExhibitionCreateOrUpdateRequest.builder()
                .name("전시회 제목")
                .startDate(LocalDate.of(2022, 4, 11))
                .endDate(LocalDate.of(2022, 6, 2))
                .genre(Genre.PAINTING)
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
        MockMultipartFile file = new MockMultipartFile("test1", "test1.PNG", MediaType.IMAGE_PNG_VALUE,
                "test1".getBytes());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);
        doThrow(IOException.class).when(amazonS3Uploader).upload(file, s3DirName);

        // when
        assertThatThrownBy(() -> {
            exhibitionAdminService.updateExhibition(0L, request, file);
        }).isInstanceOf(AWSException.class)
                .hasMessage(ErrorCode.AMAZON_S3_ERROR.getMessage());

        // then
        verify(exhibitionRepository).findById(0L);
        verify(amazonS3Uploader).upload(file, s3DirName);
    }

    @Test
    @DisplayName("전시회 장르와 설명 업데이트 테스트")
    void testSemiUpdateExhibition() {
        // given
        ExhibitionSemiUpdateRequest request = ExhibitionSemiUpdateRequest.builder()
                .description("설명")
                .genre(Genre.PAINTING)
                .build();
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);

        // when
        exhibitionAdminService.semiUpdateExhibition(0L, request);

        // then
        verify(exhibitionRepository).findById(0L);
    }

    @Test
    @DisplayName("전시회 장르만 업데이트 테스트")
    void testSemiUpdateExhibitionWithGenre() {
        // given
        ExhibitionSemiUpdateRequest request = ExhibitionSemiUpdateRequest.builder()
                .genre(Genre.PAINTING)
                .build();
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);

        // when
        exhibitionAdminService.semiUpdateExhibition(0L, request);

        // then
        verify(exhibitionRepository).findById(0L);
    }

    @Test
    @DisplayName("전시회 설명만 업데이트 테스트")
    void testSemiUpdateExhibitionWithDescription() {
        // given
        ExhibitionSemiUpdateRequest request = ExhibitionSemiUpdateRequest.builder()
                .description("설명")
                .build();
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);

        // when
        exhibitionAdminService.semiUpdateExhibition(0L, request);

        // then
        verify(exhibitionRepository).findById(0L);
    }

    @Test
    @DisplayName("전시회 soft delete 테스트")
    void testDeleteExhibition() {
        // given
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(0L);

        // when
        exhibitionAdminService.deleteExhibition(0L);

        // then
        verify(exhibitionRepository).findById(0L);
    }

    @Test
    @DisplayName("유효하지 않은 전시회 soft delete 테스트")
    void testDeleteInvalidExhibition() {
        // given
        doReturn(Optional.empty()).when(exhibitionRepository).findById(0L);

        // when // then
        assertThatThrownBy(() -> {
            exhibitionAdminService.deleteExhibition(0L);
        }).isInstanceOf(NotFoundException.class)
                .hasMessage(ErrorCode.EXHB_NOT_FOUND.getMessage());
        verify(exhibitionRepository).findById(0L);
    }
}