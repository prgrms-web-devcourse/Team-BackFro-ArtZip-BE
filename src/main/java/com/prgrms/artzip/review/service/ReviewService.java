package com.prgrms.artzip.review.service;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.error.exception.PermissionDeniedException;
import com.prgrms.artzip.common.util.AmazonS3Remover;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewLike;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.domain.repository.ReviewLikeRepository;
import com.prgrms.artzip.review.domain.repository.ReviewPhotoRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.review.dto.projection.ReviewExhibitionInfo;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.review.dto.request.ReviewUpdateRequest;
import com.prgrms.artzip.review.dto.response.ReviewExhibitionInfoResponse;
import com.prgrms.artzip.review.dto.response.ReviewIdResponse;
import com.prgrms.artzip.review.dto.response.ReviewResponse;
import com.prgrms.artzip.review.dto.response.ReviewsResponse;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private static final String REVIEW_DIRECTORY_NAME = "review/";
  private static final int REVIEW_PHOTO_COUNT = 9;

  private final CommentService commentService;

  private final ReviewRepository reviewRepository;
  private final ReviewPhotoRepository reviewPhotoRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final UserRepository userRepository;
  private final ExhibitionRepository exhibitionRepository;
  private final AmazonS3Uploader amazonS3Uploader;
  private final AmazonS3Remover amazonS3Remover;

  @Transactional
  public ReviewIdResponse createReview(final Long userId, final ReviewCreateRequest request,
      final List<MultipartFile> files) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    Exhibition exhibition = exhibitionRepository.findById(request.getExhibitionId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.EXHB_NOT_FOUND));
    validateFileCount(files);
    validateFileExtensions(files);

    Review review = Review.builder()
        .user(user)
        .exhibition(exhibition)
        .content(request.getContent())
        .title(request.getTitle())
        .date(request.getDate())
        .isPublic(request.getIsPublic())
        .build();
    Review savedReview = reviewRepository.save(review);

    if (files != null) {
      createReviewPhoto(savedReview, files);
    }

    return new ReviewIdResponse(savedReview.getId());
  }

  @Transactional
  public ReviewIdResponse updateReview(final Long userId, final Long reviewId,
      final ReviewUpdateRequest request, final List<MultipartFile> files) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
    validateUserAuthority(user, review);
    validateDeletedPhotos(review, request.getDeletedPhotos());
    validateFileCount(review, request.getDeletedPhotos(), files);
    validateFileExtensions(files);

    removeReviewPhotosByIds(request.getDeletedPhotos());

    if (files != null) {
      createReviewPhoto(review, files);
    }

    review.updateDate(request.getDate());
    review.updateTitle(request.getTitle());
    review.updateContent(request.getContent());
    review.updateIsPublic(request.getIsPublic());

    return new ReviewIdResponse(review.getId());
  }

  @Transactional
  public ReviewIdResponse removeReview(final User user, final Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
    validateUser(user);
    validateUserAuthority(user, review);

    review.updateIdDeleted(true);
    removeReviewPhotos(review.getReviewPhotos());
    removeReviewLikes(review.getReviewLikes());

    return new ReviewIdResponse(review.getId());
  }

  private void removeReviewLikes(List<ReviewLike> reviewLikes) {
    reviewLikeRepository.deleteAllInBatch(reviewLikes);
  }

  @Transactional(readOnly = true)
  public ReviewResponse getReview(final User user, final Long reviewId) {
    Review review = reviewRepository.findById(reviewId)
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

    ReviewWithLikeData reviewData = reviewRepository.findByReviewIdAndUserId(reviewId,
            Objects.isNull(user) ? null : user.getId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));

    List<ReviewPhoto> reviewPhotos = review.getReviewPhotos();
    User reviewUser = review.getUser();
    ReviewExhibitionInfo reviewExhibitionInfo = exhibitionRepository.findExhibitionForReview(
            Objects.isNull(user) ? null : user.getId(), review.getExhibition().getId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.EXHB_NOT_FOUND));
    Page<CommentResponse> comments = commentService.getCommentsByReviewId(
        reviewId, user, PageRequest.of(0, 20));
    Long reviewCommentCount = commentService.getCommentCountByReviewId(reviewId);

    return new ReviewResponse(reviewCommentCount,
        comments,
        reviewData,
        reviewPhotos,
        reviewUser,
        new ReviewExhibitionInfoResponse(reviewExhibitionInfo));
  }

  @Transactional(readOnly = true)
  public PageResponse<ReviewsResponse> getReviews(
      final User user, final Long exhibitionId, final Pageable pageable) {

    Page<ReviewWithLikeAndCommentCount> reviews = reviewRepository.findReviewsByExhibitionIdAndUserId(
        exhibitionId, Objects.isNull(user) ? null : user.getId(), pageable);

    return new PageResponse<>(reviews.map(this::getReviewsResponse));
  }

  @Transactional(readOnly = true)
  public List<ReviewsResponseForExhibitionDetail> getReviewsForExhibition(Long userId, Long exhibitionId) {
    List<ReviewWithLikeAndCommentCount> reviews = reviewRepository.findReviewsByExhibitionIdAndUserId(
        exhibitionId, Objects.isNull(userId) ? null : userId,
        PageRequest.of(0, 4, Sort.by("reviewLikeCount").descending())).getContent();

    return reviews.stream().map(r -> {
      Review review = reviewRepository.findById(r.getReviewId())
          .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
      List<ReviewPhoto> reviewPhotos = review.getReviewPhotos();
      User reviewUser = review.getUser();
      return new ReviewsResponseForExhibitionDetail(r, reviewPhotos, reviewUser);
    }).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public PageResponse<ReviewsResponse> getReviewsForMyLikes(User currentUser, Long targetUserId, Pageable pageable) {

    Page<ReviewWithLikeAndCommentCount> reviews = reviewRepository.findMyLikesReviews(
        Objects.isNull(currentUser) ? null : currentUser.getId(), targetUserId, pageable);

    return new PageResponse<>(reviews.map(this::getReviewsResponse));
  }

  @Transactional(readOnly = true)
  public PageResponse<ReviewsResponse> getMyReviews(User currentUser, Long targetUserId, Pageable pageable) {

    Page<ReviewWithLikeAndCommentCount> reviews = reviewRepository.findMyReviews(
        Objects.isNull(currentUser) ? null : currentUser.getId(), targetUserId, pageable);

    return new PageResponse<>(reviews.map(this::getReviewsResponse));
  }

  private ReviewsResponse getReviewsResponse(ReviewWithLikeAndCommentCount reviewData) {
    Review review = reviewRepository.findById(reviewData.getReviewId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_NOT_FOUND));
    List<ReviewPhoto> reviewPhotos = review.getReviewPhotos();
    User reviewUser = review.getUser();
    Exhibition exhibition = exhibitionRepository.findById(review.getExhibition().getId())
        .orElseThrow(() -> new NotFoundException(ErrorCode.EXHB_NOT_FOUND));

    return new ReviewsResponse(reviewData, reviewPhotos, reviewUser, exhibition);
  }

  private void removeReviewPhotosByIds(List<Long> reviewPhotoIds) {
    reviewPhotoIds.forEach(photoId -> {
      ReviewPhoto reviewPhoto = reviewPhotoRepository.findById(photoId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_PHOTO_NOT_FOUND));
      removeReviewPhoto(reviewPhoto);
    });
  }

  private void removeReviewPhotos(List<ReviewPhoto> reviewPhotos) {
    reviewPhotos.forEach(reviewPhoto -> {
      removeReviewPhoto(reviewPhoto);
    });
  }

  private void removeReviewPhoto(ReviewPhoto reviewPhoto) {
    amazonS3Remover.removeFile(reviewPhoto.getPath(), REVIEW_DIRECTORY_NAME);
    reviewPhotoRepository.delete(reviewPhoto);
  }

  private void createReviewPhoto(final Review review, final List<MultipartFile> files) {
    files.forEach(file -> {
      try {
        String path = amazonS3Uploader.upload(file, REVIEW_DIRECTORY_NAME + review.getId());
        reviewPhotoRepository.save(new ReviewPhoto(review, path));
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void validateFileExtensions(List<MultipartFile> files) {
    if (Objects.nonNull(files)) {
      files.forEach(this::validateFileExtension);
    }
  }

  private void validateFileExtension(final MultipartFile file) {
    String filename = file.getOriginalFilename();
    String fileExtension = filename.substring(filename.lastIndexOf("."));
    if (!(fileExtension.equalsIgnoreCase(".jpg") ||
        fileExtension.equalsIgnoreCase(".jpeg") ||
        fileExtension.equalsIgnoreCase(".png"))) {
      throw new InvalidRequestException(ErrorCode.INVALID_FILE_EXTENSION);
    }
  }

  private void validateFileCount(List<MultipartFile> files) {
    if (files != null && files.size() > REVIEW_PHOTO_COUNT) {
      throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_PHOTO_COUNT);
    }
  }

  private void validateFileCount(Review review, List<Long> deletedPhotos,
      List<MultipartFile> files) {
    Long reviewPhotoCount = reviewPhotoRepository.countByReview(review);
    int deletedPhotosCount = Objects.isNull(deletedPhotos) ? 0 : deletedPhotos.size();
    int filesCount = Objects.isNull(files) ? 0 : files.size();

    if (reviewPhotoCount - deletedPhotosCount + filesCount > REVIEW_PHOTO_COUNT) {
      throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_PHOTO_COUNT);
    }
  }

  private void validateUser(User user) {
    if (Objects.isNull(user)) {
      throw new PermissionDeniedException(ErrorCode.UNAUTHENTICATED_USER);
    }
  }

  private void validateUserAuthority(User user, Review review) {
    if (review.getUser().getId() != user.getId()) {
      throw new PermissionDeniedException(ErrorCode.NO_PERMISSION_TO_UPDATE_REVIEW);
    }
  }

  private void validateDeletedPhotos(Review review, List<Long> deletedPhotoIds) {
    List<Long> reviewPhotoIds = review.getReviewPhotos().stream()
        .map(ReviewPhoto::getId).toList();
    deletedPhotoIds.forEach(photoId -> {
      reviewPhotoRepository.findById(photoId)
          .orElseThrow(() -> new NotFoundException(ErrorCode.REVIEW_PHOTO_NOT_FOUND));
      if (!reviewPhotoIds.contains(photoId)) {
        throw new InvalidRequestException(ErrorCode.REVIEW_PHOTO_NOT_FOUND);
      }
    });
  }

  @Transactional(readOnly = true)
  public Long getReviewCountByUserId(Long userId) {
    return reviewRepository.countByUserId(userId);
  }
}
