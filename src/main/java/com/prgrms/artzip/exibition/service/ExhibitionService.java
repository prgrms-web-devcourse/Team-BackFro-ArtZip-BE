package com.prgrms.artzip.exibition.service;

import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitionService {
  private final ExhibitionRepository exhibitionRepository;

  @Transactional(readOnly = true)
  public Page<ExhibitionInfo> getUpcomingExhibitions(Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findUpcomingExhibitions(pageable);

    return exhibitionsPagingResult.map(exhibitionForSimpleQuery -> ExhibitionInfo.builder()
        .exhibitionId(exhibitionForSimpleQuery.getExhibitionId())
        .name(exhibitionForSimpleQuery.getName())
        .thumbnail(exhibitionForSimpleQuery.getThumbnail())
        .startDate(exhibitionForSimpleQuery.getPeriod().getStartDate())
        .endDate(exhibitionForSimpleQuery.getPeriod().getEndDate())
        .likeCount(exhibitionForSimpleQuery.getLikeCount())
        .reviewCount(exhibitionForSimpleQuery.getReviewCount())
        .build());
  }

}