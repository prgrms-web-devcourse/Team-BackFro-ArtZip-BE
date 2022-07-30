package com.prgrms.artzip.exibition.domain.repository;

import com.prgrms.artzip.exibition.dto.ExhibitionForSimpleQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExhibitionCustomRepository {
   Page<ExhibitionForSimpleQuery> findUpcomingExhibition(Pageable pageable);
}
