package com.prgrms.artzip.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.error.exception.PermissionDeniedException;
import com.prgrms.artzip.common.util.AmazonS3Remover;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.domain.repository.ReviewPhotoRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.review.dto.projection.ReviewExhibitionInfo;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.review.dto.request.ReviewUpdateRequest;
import com.prgrms.artzip.review.dto.response.ReviewIdResponse;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @InjectMocks
  private ReviewService reviewService;

  @Mock
  private CommentService commentService;

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

  @Mock
  AmazonS3Remover amazonS3Remover;

  private User user = new User("test@example.com", "Emily", List.of(new Role(Authority.USER)));
  private Exhibition exhibition = Exhibition.builder()
      .seq(32)
      .name("전시회 제목")
      .startDate(LocalDate.of(2022, 4, 11))
      .endDate(LocalDate.of(2022, 6, 2))
      .genre(Genre.SHOW)
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

  @Nested
  @DisplayName("리뷰 생성")
  class ReviewCreationTest {

    ReviewCreateRequest request = ReviewCreateRequest.builder()
        .exhibitionId(1L)
        .date(LocalDate.of(2022, 4, 11))
        .title("리뷰 제목입니다.")
        .content("리뷰 내용입니다.")
        .isPublic(true)
        .build();

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

      @Test
      @DisplayName("파일 개수가 최대 개수를 초과하면 InvalidRequestException 발생")
      void invokeFileCountInvalidRequestException() {
        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(exhibition)).when(exhibitionRepository)
            .findById(request.getExhibitionId());

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
                "test3.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test3".getBytes()),
            new MockMultipartFile(
                "test4",
                "test4.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test4".getBytes()),
            new MockMultipartFile(
                "test5",
                "test5.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test5".getBytes()),
            new MockMultipartFile(
                "test6",
                "test6.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test6".getBytes()),
            new MockMultipartFile(
                "test7",
                "test7.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test7".getBytes()),
            new MockMultipartFile(
                "test8",
                "test8.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test8".getBytes()),
            new MockMultipartFile(
                "test9",
                "test9.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test9".getBytes()),
            new MockMultipartFile(
                "test10",
                "test10.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                "test10".getBytes())
        );

        assertThatThrownBy(() -> reviewService.createReview(user.getId(), request, files))
            .isInstanceOf(InvalidRequestException.class);
      }
    }
  }

  @Nested
  @DisplayName("후기 수정")
  class ReviewUpdateTest {

    List<MultipartFile> filesToAdd = List.of(
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
            "test3.png",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test3".getBytes())
    );

    List<ReviewPhoto> reviewPhotosBeforeUpdate = List.of(
        new ReviewPhoto(review, "https://s3-review-photo.png"),
        new ReviewPhoto(review, "https://s3-review-photo.jpeg"),
        new ReviewPhoto(review, "https://s3-review-photo.png"),
        new ReviewPhoto(review, "https://s3-review-photo.png")
    );

    List<ReviewPhoto> reviewPhotosToDelete = reviewPhotosBeforeUpdate.subList(0, 2);

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("후기 사진 변경(삭제, 추가)이 없이 날짜, 제목, 내용, 공개 여부만 수정하는 경우 후기 수정이 정상적으로 작동")
      void testReviewUpdateWithoutPhotoChanges() {

        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(Collections.emptyList())
            .build();

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

        ReviewIdResponse response = reviewService.updateReview(user.getId(),
            review.getId(), request, null);

        assertThat(response.getReviewId()).isEqualTo(review.getId());
        Optional<Review> maybeReview = reviewRepository.findById(response.getReviewId());
        assertThat(maybeReview.isPresent()).isTrue();
        assertThat(maybeReview.get().getContent()).isEqualTo(request.getContent());
        assertThat(maybeReview.get().getDate()).isEqualTo(request.getDate());
        assertThat(maybeReview.get().getTitle()).isEqualTo(request.getTitle());
        assertThat(maybeReview.get().getIsPublic()).isEqualTo(request.getIsPublic());
      }

      @Test
      @DisplayName("후기 사진 삭제와 날짜, 제목, 내용, 공개 여부 수정하는 경우 후기 수정이 정상적으로 작동")
      void testReviewUpdateWithPhotoDeletion() {
        // given
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(
                reviewPhotosToDelete.stream().map(ReviewPhoto::getId).collect(Collectors.toList()))
            .build();

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doReturn(Optional.of(reviewPhotosToDelete.get(0)), Optional.of(reviewPhotosToDelete.get(1)))
            .when(reviewPhotoRepository).findById(any());

        // when
        ReviewIdResponse response = reviewService.updateReview(user.getId(),
            review.getId(), request, null);

        // then
        verify(amazonS3Remover, times(2)).removeFile(any(), any());
        verify(reviewPhotoRepository, times(2)).delete(any());

        assertThat(response.getReviewId()).isEqualTo(review.getId());
        Optional<Review> maybeReview = reviewRepository.findById(response.getReviewId());
        assertThat(maybeReview.isPresent()).isTrue();
        assertThat(maybeReview.get().getContent()).isEqualTo(request.getContent());
        assertThat(maybeReview.get().getDate()).isEqualTo(request.getDate());
        assertThat(maybeReview.get().getTitle()).isEqualTo(request.getTitle());
        assertThat(maybeReview.get().getIsPublic()).isEqualTo(request.getIsPublic());
      }

      @Test
      @DisplayName("후기 사진 추가와 날짜, 제목, 내용, 공개 여부 수정하는 경우 후기 수정이 정상적으로 작동")
      void testReviewUpdateWithPhotoAddition() throws IOException {
        // given
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(Collections.emptyList())
            .build();

        ReviewPhoto reviewPhotoToAdd1 = mock(ReviewPhoto.class);
        ReviewPhoto reviewPhotoToAdd2 = mock(ReviewPhoto.class);
        ReviewPhoto reviewPhotoToAdd3 = mock(ReviewPhoto.class);

        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";
        String url3 = "https://www.example3.com";

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

        doReturn(url1, url2, url3)
            .when(amazonS3Uploader).upload(any(), any());
        doReturn(reviewPhotoToAdd1, reviewPhotoToAdd2, reviewPhotoToAdd3)
            .when(reviewPhotoRepository).save(any());

        // when
        ReviewIdResponse response = reviewService.updateReview(user.getId(), review.getId(),
            request, filesToAdd);

        // then
        verify(amazonS3Uploader, times(3)).upload(any(), any());
        verify(reviewPhotoRepository, times(3)).save(any());

        assertThat(response.getReviewId()).isEqualTo(review.getId());
        Optional<Review> maybeReview = reviewRepository.findById(response.getReviewId());
        assertThat(maybeReview.isPresent()).isTrue();
        assertThat(maybeReview.get().getContent()).isEqualTo(request.getContent());
        assertThat(maybeReview.get().getDate()).isEqualTo(request.getDate());
        assertThat(maybeReview.get().getTitle()).isEqualTo(request.getTitle());
        assertThat(maybeReview.get().getIsPublic()).isEqualTo(request.getIsPublic());
      }

      @Test
      @DisplayName("후기 사진 삭제/추가와 날짜, 제목, 내용, 공개 여부 수정하는 경우 후기 수정이 정상적으로 작동")
      void testReviewUpdateWithPhotoChanges() throws IOException {

        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(
                reviewPhotosToDelete.stream().map(ReviewPhoto::getId).collect(Collectors.toList()))
            .build();

        ReviewPhoto reviewPhotoToAdd1 = mock(ReviewPhoto.class);
        ReviewPhoto reviewPhotoToAdd2 = mock(ReviewPhoto.class);
        ReviewPhoto reviewPhotoToAdd3 = mock(ReviewPhoto.class);

        String url1 = "https://www.example1.com";
        String url2 = "https://www.example2.com";
        String url3 = "https://www.example3.com";

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

        doReturn(Optional.of(reviewPhotosToDelete.get(0)), Optional.of(reviewPhotosToDelete.get(1)))
            .when(reviewPhotoRepository).findById(any());

        doReturn(url1, url2, url3)
            .when(amazonS3Uploader).upload(any(), any());
        doReturn(reviewPhotoToAdd1, reviewPhotoToAdd2, reviewPhotoToAdd3)
            .when(reviewPhotoRepository).save(any());

        // when
        ReviewIdResponse response = reviewService.updateReview(user.getId(),
            review.getId(), request, filesToAdd);

        // then
        verify(amazonS3Remover, times(2)).removeFile(any(), any());
        verify(reviewPhotoRepository, times(2)).delete(any());

        verify(amazonS3Uploader, times(3)).upload(any(), any());
        verify(reviewPhotoRepository, times(3)).save(any());

        assertThat(response.getReviewId()).isEqualTo(review.getId());
        Optional<Review> maybeReview = reviewRepository.findById(response.getReviewId());
        assertThat(maybeReview.isPresent()).isTrue();
        assertThat(maybeReview.get().getContent()).isEqualTo(request.getContent());
        assertThat(maybeReview.get().getDate()).isEqualTo(request.getDate());
        assertThat(maybeReview.get().getTitle()).isEqualTo(request.getTitle());
        assertThat(maybeReview.get().getIsPublic()).isEqualTo(request.getIsPublic());
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      @Test
      @DisplayName("존재하지 않는 user인 경우 NotFoundException 발생")
      void invokeUserNotFoundExceptionTest() {
        // given
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(Collections.emptyList())
            .build();

        doThrow(new NotFoundException(ErrorCode.USER_NOT_FOUND))
            .when(userRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.updateReview(user.getId(), review.getId(), request, null);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.USER_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("존재하지 않는 review인 경우 NotFoundException 발생")
      void invokeReviewNotFoundExceptionTest() {
        // given
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(Collections.emptyList())
            .build();

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.updateReview(user.getId(), review.getId(), request, null);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("존재하지 않는 reviewPhoto를 삭제하려는 경우 NotFoundException 발생")
      void invokeReviewPhotoNotFoundExceptionTest() {
        // given
        ReviewUpdateRequest request = ReviewUpdateRequest.builder()
            .date(LocalDate.of(2022, 4, 22))
            .title("수정된 리뷰 제목입니다.")
            .content("수정된 리뷰 내용입니다.")
            .isPublic(false)
            .deletedPhotos(
                reviewPhotosToDelete.stream().map(ReviewPhoto::getId).collect(Collectors.toList()))
            .build();

        doReturn(Optional.of(user)).when(userRepository).findById(user.getId());
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doThrow(new NotFoundException(ErrorCode.REVIEW_PHOTO_NOT_FOUND))
            .when(reviewPhotoRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.updateReview(user.getId(), review.getId(), request, null);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_PHOTO_NOT_FOUND.getMessage());
      }

    }

  }

  @Nested
  @DisplayName("후기 삭제")
  class ReviewDeletionTest {

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("후기에 reviewPhoto가 없는 경우 후기 삭제 정상 작동")
      void testReviewSoftDeletion() {
        // given
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

        // when
        ReviewIdResponse response = reviewService.removeReview(user, review.getId());

        // then
        assertThat(response.getReviewId()).isEqualTo(review.getId());
        Optional<Review> maybeReview = reviewRepository.findById(response.getReviewId());
        assertThat(maybeReview.isPresent()).isTrue();
        assertThat(maybeReview.get().getIsDeleted()).isEqualTo(true);
      }

      @Test
      @DisplayName("후기에 reviewPhoto가 있는 경우 후기 삭제 정상 작동")
      void testReviewSoftDeletionWithReviewPhoto() {
        // given
        int reviewPhotoCount = 4;
        for (int i = 0; i < reviewPhotoCount; i++) {
          new ReviewPhoto(review, "https://s3-review-photo.png");
        }

        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

        // when
        ReviewIdResponse response = reviewService.removeReview(user, review.getId());

        // then
        verify(amazonS3Remover, times(reviewPhotoCount)).removeFile(any(), any());
        verify(reviewPhotoRepository, times(reviewPhotoCount)).delete(any());

        assertThat(response.getReviewId()).isEqualTo(review.getId());
        Optional<Review> maybeReview = reviewRepository.findById(response.getReviewId());
        assertThat(maybeReview.isPresent()).isTrue();
        assertThat(maybeReview.get().getIsDeleted()).isEqualTo(true);
      }
    }

    @Nested
    @DisplayName("실패")
    class Failure {

      @Test
      @DisplayName("존재하지 않는 review인 경우 NotFoundException 발생")
      void invokeReviewNotFoundExceptionTest() {
        // given
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.removeReview(user, review.getId());
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("user == null인 경우 PermissionDeniedException 발생")
      void invokeUserPermissionDeniedExceptionTest() {
        // given
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.removeReview(null, review.getId());
        }).isInstanceOf(PermissionDeniedException.class)
            .hasMessageContaining(ErrorCode.UNAUTHENTICATED_USER.getMessage());
      }

    }

  }

  @Nested
  @DisplayName("후기 단건 조회")
  class TestGetReview {

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("user == null인 경우 후기 단건 조회 성공 (좋아요 여부 포함X)")
      void testUserIsNull() {
        User reflectionUser = new User(
            "test@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        ReviewWithLikeAndCommentCount reviewData = new ReviewWithLikeAndCommentCount(
            1L,
            reflectionReview.getDate(),
            reflectionReview.getTitle(),
            reflectionReview.getContent(),
            reflectionReview.getCreatedAt(),
            reflectionReview.getUpdatedAt(),
            true,
            false,
            0L,
            0L
        );

        ReviewExhibitionInfo reviewExhibitionInfo = new ReviewExhibitionInfo(
            reflectionExhibition.getId(),
            reflectionExhibition.getName(),
            reflectionExhibition.getThumbnail(),
            reflectionExhibition.getPeriod().getStartDate(),
            reflectionExhibition.getPeriod().getEndDate(),
            false,
            0L,
            1L);

        Comment comment = Comment.builder()
            .review(reflectionReview)
            .content("댓글 내용입니다.")
            .user(reflectionUser)
            .parent(null)
            .build();
        ReflectionTestUtils.setField(
            comment,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.now(),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            comment,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.now(),
            LocalDateTime.class
        );

        List<CommentResponse> contents = Arrays.asList(
            new CommentResponse(
                comment,
                reflectionUser,
                List.of(
                    Comment.builder()
                        .review(reflectionReview)
                        .content("댓글1 내용입니다.")
                        .user(reflectionUser)
                        .parent(null)
                        .build(),
                    Comment.builder()
                        .review(reflectionReview)
                        .content("댓글2 내용입니다.")
                        .user(reflectionUser)
                        .parent(null)
                        .build()
                )
            ));

        Page<CommentResponse> reflectionComments = new PageImpl<>(contents,
            PageRequest.of(0, 10, Sort.by("createdAt").descending()),
            1L
        );

        // given
        given(reviewRepository.findById(reflectionReview.getId()))
            .willReturn(Optional.of(reflectionReview));
        given(reviewRepository.findByReviewIdAndUserId(
            reflectionReview.getId(), null))
            .willReturn(Optional.of(reviewData));
        given(exhibitionRepository.findExhibitionForReview(
            null, reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reviewExhibitionInfo));
        given(commentService.getCommentsByReviewId(
            reflectionReview.getId(),
            null, PageRequest.of(0, 20)))
            .willReturn(reflectionComments);

        // when
        reviewService.getReview(null, reflectionReview.getId());

        // then
        verify(reviewRepository).findByReviewIdAndUserId(reflectionReview.getId(), null);
        verify(exhibitionRepository).findExhibitionForReview(
            null, reflectionReview.getExhibition().getId());
        verify(commentService).getCommentsByReviewId(
            reflectionReview.getId(),
            null, PageRequest.of(0, 20));
      }

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함O)")
      void testUserIsNotNull() {
        User reflectionUser = new User(
            "test@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        ReviewWithLikeAndCommentCount reviewData = new ReviewWithLikeAndCommentCount(
            1L,
            reflectionReview.getDate(),
            reflectionReview.getTitle(),
            reflectionReview.getContent(),
            reflectionReview.getCreatedAt(),
            reflectionReview.getUpdatedAt(),
            true,
            false,
            0L,
            0L
        );

        ReviewExhibitionInfo reviewExhibitionInfo = new ReviewExhibitionInfo(
            reflectionExhibition.getId(),
            reflectionExhibition.getName(),
            reflectionExhibition.getThumbnail(),
            reflectionExhibition.getPeriod().getStartDate(),
            reflectionExhibition.getPeriod().getEndDate(),
            false,
            0L,
            1L);

        Comment comment = Comment.builder()
            .review(reflectionReview)
            .content("댓글 내용입니다.")
            .user(reflectionUser)
            .parent(null)
            .build();
        ReflectionTestUtils.setField(
            comment,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.now(),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            comment,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.now(),
            LocalDateTime.class
        );

        List<CommentResponse> contents = Arrays.asList(
            new CommentResponse(
                comment,
                reflectionUser,
                List.of(
                    Comment.builder()
                        .review(reflectionReview)
                        .content("댓글1 내용입니다.")
                        .user(reflectionUser)
                        .parent(null)
                        .build(),
                    Comment.builder()
                        .review(reflectionReview)
                        .content("댓글2 내용입니다.")
                        .user(reflectionUser)
                        .parent(null)
                        .build()
                )
            ));

        Page<CommentResponse> reflectionComments = new PageImpl<>(contents,
            PageRequest.of(0, 10, Sort.by("createdAt").descending()),
            1L
        );

        // given
        given(reviewRepository.findById(reflectionReview.getId()))
            .willReturn(Optional.of(reflectionReview));
        given(reviewRepository.findByReviewIdAndUserId(
            reflectionReview.getId(), reflectionUser.getId()))
            .willReturn(Optional.of(reviewData));
        given(exhibitionRepository.findExhibitionForReview(
            reflectionUser.getId(), reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reviewExhibitionInfo));
        given(commentService.getCommentsByReviewId(
            reflectionReview.getId(),
            reflectionUser, PageRequest.of(0, 20)))
            .willReturn(reflectionComments);

        // when
        reviewService.getReview(reflectionUser, reflectionReview.getId());

        // then
        verify(reviewRepository).findByReviewIdAndUserId(reflectionReview.getId(),
            reflectionUser.getId());
        verify(exhibitionRepository).findExhibitionForReview(
            reflectionUser.getId(), reflectionReview.getExhibition().getId());
        verify(commentService).getCommentsByReviewId(
            reflectionReview.getId(),
            reflectionUser, PageRequest.of(0, 20));
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      private ReviewWithLikeAndCommentCount reviewData = mock(ReviewWithLikeAndCommentCount.class);

      @Test
      @DisplayName("존재하지 않는 후기를 조회하는 경우 NotFoundException 발생")
      void testReviewNotFoundException() {
        // given
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getReview(user, review.getId());
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("삭제되거나 비공개인 후기를 조회하는 경우 NotFoundException 발생")
      void testDeletedOrPrivateReviewNotFoundException() {
        // given
        Review privateReview = Review.builder()
            .user(user)
            .exhibition(exhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(false)
            .build();

        doReturn(Optional.of(privateReview)).when(reviewRepository).findById(privateReview.getId());
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findByReviewIdAndUserId(privateReview.getId(), null);

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getReview(user, review.getId());
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

      @Test
      @DisplayName("존재하지 않는 전시회를 조회하는 경우 NotFoundException 발생")
      void testExhibitionNotFoundException() {
        // given
        doReturn(Optional.of(review)).when(reviewRepository).findById(review.getId());
        doReturn(Optional.of(reviewData))
            .when(reviewRepository).findByReviewIdAndUserId(review.getId(), null);
        doThrow(new NotFoundException(ErrorCode.EXHB_NOT_FOUND))
            .when(exhibitionRepository).findExhibitionForReview(any(), any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getReview(user, review.getId());
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.EXHB_NOT_FOUND.getMessage());
      }

    }

  }

  @Nested
  @DisplayName("후기 다건 조회")
  class TestGetReviews {

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함X)")
      void testUserIsNull() {
        User reflectionUser = new User(
            "test@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        given(reviewRepository.findReviewsByExhibitionIdAndUserId(
            reflectionExhibition.getId(), null, pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));
        given(exhibitionRepository.findById(reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reflectionExhibition));

        reviewService.getReviews(null, reflectionExhibition.getId(), pageable);

        verify(reviewRepository).findReviewsByExhibitionIdAndUserId(reflectionExhibition.getId(),
            null, pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
        verify(exhibitionRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionExhibition.getId());
      }

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함O)")
      void testUserIsNotNull() {
        User reflectionUser = new User(
            "test@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        given(reviewRepository.findReviewsByExhibitionIdAndUserId(
            reflectionExhibition.getId(), reflectionUser.getId(), pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));
        given(exhibitionRepository.findById(reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reflectionExhibition));

        reviewService.getReviews(reflectionUser, reflectionExhibition.getId(), pageable);

        verify(reviewRepository).findReviewsByExhibitionIdAndUserId(reflectionExhibition.getId(),
            reflectionUser.getId(), pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
        verify(exhibitionRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionExhibition.getId());
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      Page<ReviewWithLikeAndCommentCount> reviews = new PageImpl<>(Arrays.asList(
          new ReviewWithLikeAndCommentCount(
              1L,
              review.getDate(),
              review.getTitle(),
              review.getContent(),
              review.getCreatedAt(),
              review.getUpdatedAt(),
              true,
              false,
              0L,
              0L
          )
      ));

      @Test
      @DisplayName("존재하지 않는 후기를 조회하는 경우 NotFoundException 발생")
      void testReviewNotFoundException() {
        // given
        doReturn(reviews)
            .when(reviewRepository).findReviewsByExhibitionIdAndUserId(
                exhibition.getId(), null, pageable);
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getReviews(user, exhibition.getId(), pageable);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

    }

  }

  @Nested
  @DisplayName("전시회 상세를 위한 후기 다건 조회")
  class TestGetReviewsForExhibition {

    Pageable pageable = PageRequest.of(0, 4, Sort.by("reviewLikeCount").descending());

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함X)")
      void testUserIsNull() {
        User reflectionUser = new User(
            "test@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        given(reviewRepository.findReviewsByExhibitionIdAndUserId(
            reflectionExhibition.getId(), null, pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));

        reviewService.getReviewsForExhibition(null, reflectionExhibition.getId());

        verify(reviewRepository).findReviewsByExhibitionIdAndUserId(reflectionExhibition.getId(),
            null, pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
      }

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함O)")
      void testUserIsNotNull() {
        User reflectionUser = new User(
            "test@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        given(reviewRepository.findReviewsByExhibitionIdAndUserId(
            reflectionExhibition.getId(), reflectionUser.getId(), pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));

        reviewService.getReviewsForExhibition(reflectionUser.getId(), reflectionExhibition.getId());

        verify(reviewRepository).findReviewsByExhibitionIdAndUserId(reflectionExhibition.getId(),
            reflectionUser.getId(), pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      Review review = new Review(user,
          exhibition,
          "리뷰 내용",
          "리뷰 제목",
          LocalDate.now(),
          true);

      Page<ReviewWithLikeAndCommentCount> reviews = new PageImpl<>(Arrays.asList(
          new ReviewWithLikeAndCommentCount(
              1L,
              review.getDate(),
              review.getTitle(),
              review.getContent(),
              review.getCreatedAt(),
              review.getUpdatedAt(),
              true,
              false,
              0L,
              0L
          )
      ));

      @Test
      @DisplayName("존재하지 않는 후기를 조회하는 경우 NotFoundException 발생")
      void testReviewNotFoundException() {
        // given
        doReturn(reviews)
            .when(reviewRepository).findReviewsByExhibitionIdAndUserId(
                exhibition.getId(), null, pageable);
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getReviewsForExhibition(user.getId(), exhibition.getId());
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

    }

  }

  @Nested
  @DisplayName("특정 유저가 좋아요 표시한 후기 다건 조회")
  class TestGetReviewsForMyLikes {

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함X)")
      void testUserIsNull() {
        User reflectionCurrentUser = new User(
            "test1@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionCurrentUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        User reflectionTargetUser = new User(
            "test2@example.com",
            "choonsik",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionTargetUser,
            User.class,
            "id",
            2L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionCurrentUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        ReviewLike reviewLikeOfTarget = new ReviewLike(reflectionReview, reflectionTargetUser);
        ReflectionTestUtils.setField(
            reviewLikeOfTarget,
            ReviewLike.class,
            "id",
            1L,
            Long.class
        );

        // given
        given(reviewRepository.findMyLikesReviews(
            null, reflectionTargetUser.getId(), pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));
        given(exhibitionRepository.findById(reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reflectionExhibition));

        // when
        reviewService.getReviewsForMyLikes(null, reflectionTargetUser.getId(), pageable);

        // when
        verify(reviewRepository).findMyLikesReviews(
            null, reflectionTargetUser.getId(), pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
        verify(exhibitionRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionExhibition.getId());
      }

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함O)")
      void testUserIsNotNull() {
        User reflectionCurrentUser = new User(
            "test1@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionCurrentUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        User reflectionTargetUser = new User(
            "test2@example.com",
            "choonsik",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionTargetUser,
            User.class,
            "id",
            2L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
            .name("전시회 제목")
            .startDate(LocalDate.of(2022, 4, 11))
            .endDate(LocalDate.of(2022, 6, 2))
            .genre(Genre.INSTALLATION)
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
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionCurrentUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        ReviewLike reviewLikeOfTarget = new ReviewLike(reflectionReview, reflectionTargetUser);
        ReflectionTestUtils.setField(
            reviewLikeOfTarget,
            ReviewLike.class,
            "id",
            1L,
            Long.class
        );

        // given
        given(reviewRepository.findMyLikesReviews(
            reflectionCurrentUser.getId(), null, pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));
        given(exhibitionRepository.findById(reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reflectionExhibition));

        // when
        reviewService.getReviewsForMyLikes(reflectionCurrentUser, null, pageable);

        // when
        verify(reviewRepository).findMyLikesReviews(
            reflectionCurrentUser.getId(), null, pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
        verify(exhibitionRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionExhibition.getId());
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      Page<ReviewWithLikeAndCommentCount> reviews = new PageImpl<>(Arrays.asList(
          new ReviewWithLikeAndCommentCount(
              1L,
              review.getDate(),
              review.getTitle(),
              review.getContent(),
              review.getCreatedAt(),
              review.getUpdatedAt(),
              true,
              false,
              0L,
              0L
          )
      ));

      @Test
      @DisplayName("존재하지 않는 후기를 조회하는 경우 NotFoundException 발생")
      void testReviewNotFoundException() {
        // given
        doReturn(reviews)
            .when(reviewRepository).findMyLikesReviews(
                null, user.getId(), pageable);
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getReviewsForMyLikes(null, user.getId(), pageable);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

    }

  }

  @Nested
  @DisplayName("특정 유저가 작성한 후기 다건 조회")
  class TestGetMyReviews {

    Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

    @Nested
    @DisplayName("성공")
    class Success {

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함X)")
      void testUserIsNull() {
        User reflectionCurrentUser = new User(
            "test1@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionCurrentUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        User reflectionTargetUser = new User(
            "test2@example.com",
            "choonsik",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionTargetUser,
            User.class,
            "id",
            2L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
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
            .thumbnail("https://www.image-example.com")
            .url("https://www.example.com")
            .placeUrl("https://www.place-example.com")
            .build();
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionCurrentUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        ReviewLike reviewLikeOfTarget = new ReviewLike(reflectionReview, reflectionTargetUser);
        ReflectionTestUtils.setField(
            reviewLikeOfTarget,
            ReviewLike.class,
            "id",
            1L,
            Long.class
        );

        // given
        given(reviewRepository.findMyReviews(
            null, reflectionTargetUser.getId(), pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));
        given(exhibitionRepository.findById(reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reflectionExhibition));

        // when
        reviewService.getMyReviews(null, reflectionTargetUser.getId(), pageable);

        // when
        verify(reviewRepository).findMyReviews(
            null, reflectionTargetUser.getId(), pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
        verify(exhibitionRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionExhibition.getId());
      }

      @Test
      @DisplayName("user == null인 경우 후기 다건 조회 성공 (좋아요 여부 포함O)")
      void testUserIsNotNull() {
        User reflectionCurrentUser = new User(
            "test1@example.com",
            "Emily",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionCurrentUser,
            User.class,
            "id",
            1L,
            Long.class
        );

        User reflectionTargetUser = new User(
            "test2@example.com",
            "choonsik",
            List.of(new Role(Authority.USER)));
        ReflectionTestUtils.setField(
            reflectionTargetUser,
            User.class,
            "id",
            2L,
            Long.class
        );

        Exhibition reflectionExhibition = Exhibition.builder()
            .seq(32)
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
            .thumbnail("https://www.image-example.com")
            .url("https://www.example.com")
            .placeUrl("https://www.place-example.com")
            .build();
        ReflectionTestUtils.setField(
            reflectionExhibition,
            Exhibition.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionExhibition,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 4, 11, 1, 36),
            LocalDateTime.class
        );

        Review reflectionReview = Review.builder()
            .user(reflectionCurrentUser)
            .exhibition(reflectionExhibition)
            .content("이것은 리뷰 본문입니다.")
            .title("이것은 리뷰 제목입니다.")
            .date(LocalDate.now())
            .isPublic(true)
            .build();
        ReflectionTestUtils.setField(
            reflectionReview,
            Review.class,
            "id",
            1L,
            Long.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "createdAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );
        ReflectionTestUtils.setField(
            reflectionReview,
            BaseEntity.class,
            "updatedAt",
            LocalDateTime.of(2022, 5, 5, 15, 6),
            LocalDateTime.class
        );

        List<ReviewWithLikeAndCommentCount> contents = Arrays.asList(
            new ReviewWithLikeAndCommentCount(
                1L,
                reflectionReview.getDate(),
                reflectionReview.getTitle(),
                reflectionReview.getContent(),
                reflectionReview.getCreatedAt(),
                reflectionReview.getUpdatedAt(),
                true,
                false,
                0L,
                0L
            ));

        Page<ReviewWithLikeAndCommentCount> reflectionReviews = new PageImpl<>(contents,
            pageable,
            1L
        );

        ReviewLike reviewLikeOfTarget = new ReviewLike(reflectionReview, reflectionTargetUser);
        ReflectionTestUtils.setField(
            reviewLikeOfTarget,
            ReviewLike.class,
            "id",
            1L,
            Long.class
        );

        // given
        given(reviewRepository.findMyReviews(
            reflectionCurrentUser.getId(), null, pageable))
            .willReturn(reflectionReviews);
        given(reviewRepository.findById(reflectionReviews.getContent().get(0).getReviewId()))
            .willReturn(Optional.of(reflectionReview));
        given(exhibitionRepository.findById(reflectionReview.getExhibition().getId()))
            .willReturn(Optional.of(reflectionExhibition));

        // when
        reviewService.getMyReviews(reflectionCurrentUser, null, pageable);

        // when
        verify(reviewRepository).findMyReviews(
            reflectionCurrentUser.getId(), null, pageable);
        verify(reviewRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionReview.getId());
        verify(exhibitionRepository, times(reflectionReviews.getContent().size())).findById(
            reflectionExhibition.getId());
      }

    }

    @Nested
    @DisplayName("실패")
    class Failure {

      Page<ReviewWithLikeAndCommentCount> reviews = new PageImpl<>(Arrays.asList(
          new ReviewWithLikeAndCommentCount(
              1L,
              review.getDate(),
              review.getTitle(),
              review.getContent(),
              review.getCreatedAt(),
              review.getUpdatedAt(),
              true,
              false,
              0L,
              0L
          )
      ));

      @Test
      @DisplayName("존재하지 않는 후기를 조회하는 경우 NotFoundException 발생")
      void testReviewNotFoundException() {
        // given
        doReturn(reviews)
            .when(reviewRepository).findMyReviews(
                null, user.getId(), pageable);
        doThrow(new NotFoundException(ErrorCode.REVIEW_NOT_FOUND))
            .when(reviewRepository).findById(any());

        // when
        // then
        assertThatThrownBy(() -> {
          reviewService.getMyReviews(null, user.getId(), pageable);
        }).isInstanceOf(NotFoundException.class)
            .hasMessageContaining(ErrorCode.REVIEW_NOT_FOUND.getMessage());
      }

    }

  }
}