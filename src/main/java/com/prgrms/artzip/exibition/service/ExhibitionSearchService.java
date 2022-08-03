package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_QUERY;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_QUERY_FOR_REVIEW;
import static java.util.Objects.isNull;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.response.ExhibitionBasicInfo;
import com.prgrms.artzip.exibition.dto.response.ExhibitionInfo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionSearchService {

  private final ExhibitionRepository exhibitionRepository;

  public Page<ExhibitionInfo> getExhibitionsByQuery(Long userId, String query, boolean includeEnd,
      Pageable pageable) {
    if (isNull(query) || query.isBlank() || query.length() < 2) {
      throw new InvalidRequestException(INVALID_EXHB_QUERY);
    }

    return exhibitionRepository.findExhibitionsByQuery(userId, query, includeEnd, pageable)
        .map(ExhibitionInfo::new);
  }

  public List<ExhibitionBasicInfo> getExhibitionsForReview(String query) {
    if (isNull(query) || query.isBlank()) {
      throw new InvalidRequestException(INVALID_EXHB_QUERY_FOR_REVIEW);
    }

    return exhibitionRepository.findExhibitionsForReview(query).stream()
        .map(exhibitionBasicForSimpleQuery -> ExhibitionBasicInfo.builder()
            .exhibitionId(exhibitionBasicForSimpleQuery.getId())
            .name(exhibitionBasicForSimpleQuery.getName())
            .thumbnail(exhibitionBasicForSimpleQuery.getThumbnail())
            .build())
        .collect(Collectors.toList());
  }

}
