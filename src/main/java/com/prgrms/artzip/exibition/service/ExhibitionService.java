package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionLikeRepository;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exibition.dto.response.ExhibitionDetailInfo;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionService {

  private final ExhibitionRepository exhibitionRepository;
  private final ExhibitionLikeRepository exhibitionLikeRepository;

  public Page<ExhibitionInfo> getUpcomingExhibitions(Long userId, Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
        .findUpcomingExhibitions(userId, pageable);

    return exhibitionsPagingResult.map(
        (ExhibitionForSimpleQuery exhibitionForSimpleQuery) -> new ExhibitionInfo(
            exhibitionForSimpleQuery));
  }

  public Page<ExhibitionInfo> getMostLikeExhibitions(Long userId, boolean includeEnd,
      Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
        .findMostLikeExhibitions(userId, includeEnd, pageable);
    return exhibitionsPagingResult.map(ExhibitionInfo::new);
  }
  
  public ExhibitionDetailInfo getExhibition(Long userId, Long exhibitionId) {
    ExhibitionDetailForSimpleQuery exhibition = exhibitionRepository
        .findExhibition(userId, exhibitionId)
        .orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));

    // getReviews()

    return ExhibitionDetailInfo.builder()
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
        .build();
  }
}