package com.prgrms.artzip.exibition.service;

import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionService {
  private final ExhibitionRepository exhibitionRepository;

  public Page<ExhibitionInfo> getUpcomingExhibitions(Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUpcomingExhibitions(pageable);

    return exhibitionsPagingResult.map(this::exhibitionForSimpleQueryToExhibitionInfo);
  }

  public Page<ExhibitionInfo> getMostLikeExhibitions(boolean includeEnd, Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findMostLikeExhibitions(includeEnd, pageable);

    return exhibitionsPagingResult.map(this::exhibitionForSimpleQueryToExhibitionInfo);
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
}