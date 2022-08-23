package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionAroundMeInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionDetailInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import com.prgrms.artzip.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.prgrms.artzip.common.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ExhibitionService {
    private final ExhibitionRepository exhibitionRepository;
    private final ReviewService reviewService;

    public Page<ExhibitionInfoResponse> getUpcomingExhibitions(Long userId, Pageable pageable) {
        Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUpcomingExhibitions(userId, pageable);

        return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
    }

    public Page<ExhibitionInfoResponse> getMostLikeExhibitions(Long userId, boolean includeEnd, Pageable pageable) {
        Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findMostLikeExhibitions(userId, includeEnd, pageable);

        return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
    }

    public ExhibitionDetailInfoResponse getExhibition(Long userId, Long exhibitionId) {
        ExhibitionDetailForSimpleQuery exhibition = exhibitionRepository.findExhibition(userId, exhibitionId).orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));
        List<ReviewsResponseForExhibitionDetail> reviews = reviewService.getReviewsForExhibition(userId, exhibitionId);

        return new ExhibitionDetailInfoResponse(exhibition, reviews);
    }

    public Page<ExhibitionInfoResponse> getUserLikeExhibitions(Long userId, Long exhibitionLikeUserId, Pageable pageable) {
        Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUserLikeExhibitions(userId, exhibitionLikeUserId, pageable);

        return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
    }

    public List<ExhibitionAroundMeInfoResponse> getExhibitionsAroundMe(Long userId, double latitude, double longitude, double distance) {
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
}