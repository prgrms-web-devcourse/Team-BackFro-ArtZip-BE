package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCustomRepository {

  Page<ExhibitionForSimpleQuery> findUpcomingExhibitions(Pageable pageable);

  Page<ExhibitionForSimpleQuery> findMostLikeExhibitions(boolean includeEnd, Pageable pageable);

  Optional<ExhibitionDetailForSimpleQuery> findExhibition(Long exhibitionId);

  Page<ExhibitionForSimpleQuery> findExhibitionsByQuery(String query, boolean includeEnd,
      Pageable pageable);

  // findExhibitionsForReview

}
