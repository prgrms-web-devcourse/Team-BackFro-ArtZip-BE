package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;
import static com.prgrms.artzip.common.ErrorCode.INVALID_COORDINATE;
import static com.prgrms.artzip.common.ErrorCode.INVALID_DISTANCE;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionAroundMeInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionDetailInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import com.prgrms.artzip.user.domain.User;
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

@Service
@RequiredArgsConstructor
public class ExhibitionService {

  private final ExhibitionRepository exhibitionRepository;
  private final ReviewRepository reviewRepository;

  public Page<ExhibitionInfoResponse> getUpcomingExhibitions(Long userId,
      Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
        .findUpcomingExhibitions(userId, pageable);
    return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
  }

  public Page<ExhibitionInfoResponse> getMostLikeExhibitions(Long userId,
      boolean includeEnd,
      Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
        .findMostLikeExhibitions(userId, includeEnd, pageable);
    return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
  }

  @Transactional(readOnly = true)
  public ExhibitionDetailInfoResponse getExhibition(Long userId, Long exhibitionId) {
    ExhibitionDetailForSimpleQuery exhibition = exhibitionRepository
        .findExhibition(userId, exhibitionId)
        .orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));

    List<ReviewsResponseForExhibitionDetail> reviews = getReviews(userId, exhibitionId);

    return ExhibitionDetailInfoResponse.builder()
        .exhibitionId(exhibition.getId())
        .name(exhibition.getName())
        .thumbnail(exhibition.getThumbnail())
        .startDate(exhibition.getPeriod().getStartDate())
        .endDate(exhibition.getPeriod().getEndDate())
        .area(exhibition.getLocation().getArea())
        .url(hasText(exhibition.getUrl()) ? exhibition.getUrl() : null)
        .placeUrl(hasText(exhibition.getPlaceUrl()) ? exhibition.getPlaceUrl() : null)
        .inquiry(exhibition.getInquiry())
        .genre(exhibition.getGenre())
        .description(exhibition.getDescription())
        .likeCount(exhibition.getLikeCount())
        .placeAddress(exhibition.getLocation().getAddress())
        .lat(exhibition.getLocation().getLatitude())
        .lng(exhibition.getLocation().getLongitude())
        .isLiked(exhibition.getIsLiked())
        .reviews(reviews)
        .build();
  }

  public Page<ExhibitionInfoResponse> getUserLikeExhibitions(Long userId,
      Long exhibitionLikeUserId,
      Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
        .findUserLikeExhibitions(userId, exhibitionLikeUserId, pageable);

    return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
  }

  public List<ExhibitionAroundMeInfoResponse> getExhibitionsAroundMe(Long userId, double latitude,
      double longitude, double distance) {
    validateCoordinate(latitude, longitude);
    validateDistance(distance);

    List<ExhibitionWithLocationForSimpleQuery> exhibitions = exhibitionRepository.findExhibitionsAroundMe(
        userId, latitude, longitude, distance);

    return exhibitions.stream()
        .map(ExhibitionAroundMeInfoResponse::new)
        .collect(Collectors.toList());
  }

  private void validateCoordinate(double latitude, double longitude) {
    if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
      throw new InvalidRequestException(INVALID_COORDINATE);
    }
  }

  private void validateDistance(double distance) {
    if (distance <= 0) {
      throw new InvalidRequestException(INVALID_DISTANCE);
    }
  }

  private List<ReviewsResponseForExhibitionDetail> getReviews(Long userId, Long exhibitionId) {
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

}