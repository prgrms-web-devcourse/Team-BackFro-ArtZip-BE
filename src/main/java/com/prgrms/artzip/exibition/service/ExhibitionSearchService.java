package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_QUERY_BLANK;
import static java.util.Objects.isNull;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionSearchService {

  private final ExhibitionRepository exhibitionRepository;

  public Page<ExhibitionInfo> getExhibitionByQuery(String query, boolean includeEnd,
      Pageable pageable) {
    validateQuery(query);
    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findExhibitionsByQuery(
        query, includeEnd, pageable);

    return exhibitionsPagingResult.map(ExhibitionInfo::new);
  }

  public List<ExhibitionBasicForSimpleQuery> getExhibitionsForReview(String query) {
    validateQuery(query);
    return exhibitionRepository.findExhibitionsForReview(query);
  }


  private void validateQuery(String query) {
    if (isNull(query) || query.isBlank()) {
      throw new InvalidRequestException(EXHB_QUERY_BLANK);
    }
  }

}
