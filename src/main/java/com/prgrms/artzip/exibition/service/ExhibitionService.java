package com.prgrms.artzip.exibition.service;

import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import com.prgrms.artzip.user.domain.User;
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

  public void getExhibitionDetail(Long exhibitionId) {
    User user = null;

    // 로그인 여부를 확인
      // 로그인 & 좋아요 => true
      // !로그인 & 좋아요 => false
      // 로그인 & !좋아요 => false;

    // projection
      // Exhibition + 좋아요 개수
      // 좋아요 여부(?)
      // reviews => 이건 따로 가져와야함


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