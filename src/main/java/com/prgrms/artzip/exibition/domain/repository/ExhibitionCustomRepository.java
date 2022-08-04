package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
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
}
