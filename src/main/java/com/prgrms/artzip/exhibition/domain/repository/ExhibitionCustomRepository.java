package com.prgrms.artzip.exhibition.domain.repository;

import com.prgrms.artzip.exhibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import com.prgrms.artzip.review.dto.response.ReviewExhibitionInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCustomRepository {

  Page<ExhibitionForSimpleQuery> findUpcomingExhibitions(Long userId, Pageable pageable);

  Page<ExhibitionForSimpleQuery> findMostLikeExhibitions(Long userId, boolean includeEnd,
      Pageable pageable);

  Optional<ExhibitionDetailForSimpleQuery> findExhibition(Long userId, Long exhibitionId);

  Page<ExhibitionForSimpleQuery> findExhibitionsByQuery(Long userId, String query,
      boolean includeEnd, Pageable pageable);

  List<ExhibitionBasicForSimpleQuery> findExhibitionsForReview(String query);

  Page<ExhibitionForSimpleQuery> findUserLikeExhibitions(Long userId,
      Long exhibitionLikeUserId, Pageable pageable);

  Page<ExhibitionForSimpleQuery> findExhibitionsByCustomCondition(Long userId,
      ExhibitionCustomCondition exhibitionCustomCondition, Pageable pageable);

  List<ExhibitionWithLocationForSimpleQuery> findExhibitionAroundMe(Long userId, double latitude,
      double longitude, double distance);

  Optional<ReviewExhibitionInfo> findExhibitionForReview(Long userId, Long exhibitionId);
}
