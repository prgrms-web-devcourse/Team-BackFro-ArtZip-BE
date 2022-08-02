package com.prgrms.artzip.review.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exibition.domain.Area;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.Genre;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.domain.repository.ReviewPhotoRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.review.dto.ReviewCreateRequest;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @InjectMocks
  private ReviewService reviewService;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private ReviewPhotoRepository reviewPhotoRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ExhibitionRepository exhibitionRepository;

  @Mock
  AmazonS3Uploader amazonS3Uploader;

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
  private Review review = Review.builder()
      .user(user)
      .exhibition(exhibition)
      .content("이것은 리뷰 본문입니다.")
      .title("이것은 리뷰 제목입니다.")
      .date(LocalDate.now())
      .isPublic(true)
      .build();

  ReviewCreateRequest request = ReviewCreateRequest.builder()
      .exhibitionId(1L)
      .date(LocalDate.of(2022, 4, 11))
      .title("리뷰 제목입니다.")
      .content("리뷰 내용입니다.")
      .isPublic(true)
      .build();

  @Nested
  @DisplayName("리뷰 생성")
  class ReviewCreationTest {

    List<MultipartFile> files = List.of(
        new MockMultipartFile(
            "test1",
            "test1.png",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test1".getBytes()),
        new MockMultipartFile(
            "test2",
            "test2.jpeg",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test2".getBytes()),
        new MockMultipartFile(
            "test3",
            "test3.jpg",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test3".getBytes())
    );

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("후기 사진이 없는 경우 후기 생성이 정상적으로 작동")
      void testReviewCreationWithoutPhoto() {
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository)
            .findById(request.getExhibitionId());
        doReturn(review).when(reviewRepository).save(any());
        reviewService.createReview(user.getId(), request, null);

        verify(reviewRepository).save(any());
      }

      @Test
      @DisplayName("후기 사진이 있는 경우 후기 생성이 정상적으로 작동")
      void reviewCreationTest() throws IOException {
        ReviewPhoto reviewPhoto1 = mock(ReviewPhoto.class);
        ReviewPhoto reviewPhoto2 = mock(ReviewPhoto.class);
        ReviewPhoto reviewPhoto3 = mock(ReviewPhoto.class);

        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";
        String url3 = "https://www.example3.com";

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository)
            .findById(request.getExhibitionId());
        doReturn(review).when(reviewRepository).save(any());

        doReturn(url1).doReturn(url2).doReturn(url3)
            .when(amazonS3Uploader).upload(any(), any());

        doReturn(reviewPhoto1).doReturn(reviewPhoto2).doReturn(reviewPhoto3)
            .when(reviewPhotoRepository).save(any());

        reviewService.createReview(user.getId(), request, files);

        verify(reviewRepository).save(any());
        verify(reviewPhotoRepository, times(3)).save(any());
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      @Test
      @DisplayName("존재하지 않는 user인 경우 NotFoundException 발생")
      void invokeUserNotFoundExceptionTest() {
        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        assertThatThrownBy(() -> {
          reviewService.createReview(user.getId(), request, null);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("존재하지 않는 exhibition인 경우 NotFoundException 발생")
      void invokeExhibitionNotFoundExceptionTest() {
        doReturn(Optional.of(user)).when(userRepository).findById(any());
        doThrow(new NotFoundException(ErrorCode.EXHB_NOT_FOUND))
            .when(exhibitionRepository).findById(any());

        assertThatThrownBy(() -> {
          reviewService.createReview(user.getId(), request, null);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.EXHB_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("리뷰 이미지 파일이 올바르지 않은 파일 확장자인 경우 InvalidRequestException 발생")
      void invokeInvalidFileExtensionExceptionTest() {
        List<MultipartFile> files = List.of(
            new MockMultipartFile(
                "test1",
                "test1.pn",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test1".getBytes()),
            new MockMultipartFile(
                "test2",
                "test2.jpeeg",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test2".getBytes())
        );

        doReturn(Optional.of(user)).when(userRepository).findById(any());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository).findById(any());
        doReturn(review).when(reviewRepository).save(any());

        assertThatThrownBy(() -> {
          reviewService.createReview(user.getId(), request, files);
        }).isInstanceOf(InvalidRequestException.class)
            .hasMessageContaining(ErrorCode.INVALID_FILE_EXTENSION.getMessage());
      }

      @Test
      @DisplayName("s3에 이미지 업로드할 때 IOException 발생")
      void invokeIOException() throws IOException {
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository)
            .findById(request.getExhibitionId());
        doReturn(review).when(reviewRepository).save(any());
        doThrow(IOException.class).when(amazonS3Uploader).upload(any(), any());

        reviewService.createReview(user.getId(), request, files);
      }
    }
  }

}