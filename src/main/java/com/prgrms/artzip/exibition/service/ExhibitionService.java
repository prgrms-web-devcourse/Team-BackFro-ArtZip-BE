package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exibition.dto.response.ExhibitionDetailInfo;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import com.prgrms.artzip.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionService {
  private final ExhibitionLikeService exhibitionLikeService;
  private final ExhibitionRepository exhibitionRepository;

  public Page<ExhibitionInfo> getUpcomingExhibitions(Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUpcomingExhibitions(pageable);
    return exhibitionsPagingResult.map(this::exhibitionForSimpleQueryToExhibitionInfo);
  }

  public Page<ExhibitionInfo> getMostLikeExhibitions(boolean includeEnd, Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findMostLikeExhibitions(includeEnd, pageable);
    return exhibitionsPagingResult.map(this::exhibitionForSimpleQueryToExhibitionInfo);
  }

  public ExhibitionDetailInfo getExhibition(User user, Long exhibitionId) {
    ExhibitionDetailForSimpleQuery exhibition = exhibitionRepository.findExhibition(exhibitionId)
        .orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));

    boolean isLiked = false;
    if(user != null) {
      isLiked = exhibitionLikeService.isLikedExhibition(exhibitionId, user.getId());
    }

    // getReviews()

    return exhibitionDetailForSimpleQueryToExhibitionDetailInfo(exhibition, isLiked);
  }

  private ExhibitionInfo exhibitionForSimpleQueryToExhibitionInfo(ExhibitionForSimpleQuery exhibitionForSimpleQuery) {
    return ExhibitionInfo.builder()
        .exhibitionId(exhibitionForSimpleQuery.getId())
        .name(exhibitionForSimpleQuery.getName())
        .thumbnail(exhibitionForSimpleQuery.getThumbnail())
        .startDate(exhibitionForSimpleQuery.getPeriod().getStartDate())
        .endDate(exhibitionForSimpleQuery.getPeriod().getEndDate())
        .likeCount(exhibitionForSimpleQuery.getLikeCount())
        .reviewCount(exhibitionForSimpleQuery.getReviewCount())
        .build();
  }

  private ExhibitionDetailInfo exhibitionDetailForSimpleQueryToExhibitionDetailInfo(ExhibitionDetailForSimpleQuery exhibition, boolean isLiked) {
    return ExhibitionDetailInfo.builder()
        .exhibitionId(exhibition.getId())
        .name(exhibition.getName())
        .thumbnail(exhibition.getThumbnail())
        .startDate(exhibition.getPeriod().getStartDate())
        .endDate(exhibition.getPeriod().getEndDate())
        .area(exhibition.getLocation().getArea())
        .url(exhibition.getUrl())
        .placeUrl(exhibition.getPlaceUrl())
        .inquiry(exhibition.getInquiry())
        .genre(exhibition.getGenre())
        .description(exhibition.getDescription())
        .likeCount(exhibition.getLikeCount())
        .placeAddress(exhibition.getLocation().getAddress())
        .lat(exhibition.getLocation().getLatitude())
        .lng(exhibition.getLocation().getLongitude())
        .isLiked(isLiked)
        .build();
  }
}