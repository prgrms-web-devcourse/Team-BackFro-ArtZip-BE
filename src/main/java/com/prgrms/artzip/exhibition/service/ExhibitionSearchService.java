package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.INVALID_CUSTOM_EXHB_CONDITION;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_QUERY;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_QUERY_FOR_REVIEW;
import static com.prgrms.artzip.common.ErrorCode.INVALID_INPUT_VALUE;
import static java.util.Objects.isNull;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCustomConditionRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionBasicInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExhibitionSearchService {

  private final ExhibitionRepository exhibitionRepository;

  public Page<ExhibitionInfoResponse> getExhibitionsByQuery(Long userId, String query,
      boolean includeEnd,
      Pageable pageable) {
    if (isNull(query) || query.isBlank() || query.length() < 2) {
      throw new InvalidRequestException(INVALID_EXHB_QUERY);
    }

    return exhibitionRepository.findExhibitionsByQuery(userId, query, includeEnd, pageable)
        .map(ExhibitionInfoResponse::new);
  }

  public List<ExhibitionBasicInfoResponse> getExhibitionsForReview(String query) {
    if (isNull(query) || query.isBlank()) {
      throw new InvalidRequestException(INVALID_EXHB_QUERY_FOR_REVIEW);
    }

    return exhibitionRepository.findExhibitionsForReview(query).stream()
        .map(exhibitionBasicForSimpleQuery -> ExhibitionBasicInfoResponse.builder()
            .exhibitionId(exhibitionBasicForSimpleQuery.getId())
            .name(exhibitionBasicForSimpleQuery.getName())
            .thumbnail(exhibitionBasicForSimpleQuery.getThumbnail())
            .build())
        .collect(Collectors.toList());
  }

  public Page<ExhibitionInfoResponse> getExhibitionsByCustomCondition(Long userId,
      ExhibitionCustomConditionRequest exhibitionCustomConditionRequest, boolean includeEnd,
      Pageable pageable) {

    ExhibitionCustomCondition exhibitionCustomCondition = validateCondition(
        exhibitionCustomConditionRequest, includeEnd);

    Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository
        .findExhibitionsByCustomCondition(userId, exhibitionCustomCondition, pageable);

    return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
  }

  private ExhibitionCustomCondition validateCondition(
      ExhibitionCustomConditionRequest exhibitionCustomConditionRequest, boolean includeEnd) {
    List<Area> requestedAreas = exhibitionCustomConditionRequest.getAreas();
    List<Month> requestedMonths = exhibitionCustomConditionRequest.getMonths();

    if (requestedAreas.isEmpty() || requestedMonths.isEmpty()) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
    
    if (requestedAreas.contains(null) || requestedMonths.contains(null)) {
      throw new InvalidRequestException(INVALID_CUSTOM_EXHB_CONDITION);
    }

    Set<Area> areas = new HashSet<>();
    if (requestedAreas.contains(Area.ALL)) {
      areas.add(Area.ALL);
    } else {
      requestedAreas.forEach(area -> areas.add(area));
    }

    Set<Month> months = new HashSet<>();
    if (requestedMonths.contains(Month.ALL)) {
      months.add(Month.ALL);
    } else {
      requestedMonths.forEach(month -> months.add(month));
    }

    return ExhibitionCustomCondition.builder()
        .areas(areas)
        .months(months)
        .includeEnd(includeEnd)
        .build();
  }

}
