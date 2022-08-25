package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import com.prgrms.artzip.exhibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCustomConditionRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionBasicInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.prgrms.artzip.common.ErrorCode.*;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ExhibitionSearchService {
    private final ExhibitionRepository exhibitionRepository;

    public Page<ExhibitionInfoResponse> getExhibitionsByQuery(Long userId, String query, boolean includeEnd, Pageable pageable) {
        if (isNull(query) || query.isBlank() || query.length() < 2) {
            throw new InvalidRequestException(INVALID_EXHB_QUERY);
        }

        return exhibitionRepository.findExhibitionsByQuery(userId, query, includeEnd, pageable).map(ExhibitionInfoResponse::new);
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

    public Page<ExhibitionInfoResponse> getExhibitionsByCustomCondition(Long userId, ExhibitionCustomConditionRequest exhibitionCustomConditionRequest, boolean includeEnd, Pageable pageable) {
        ExhibitionCustomCondition exhibitionCustomCondition = validateCondition(exhibitionCustomConditionRequest, includeEnd);
        Page<ExhibitionForSimpleQuery> exhibitionsPagingResult = exhibitionRepository.findExhibitionsByCustomCondition(userId, exhibitionCustomCondition, pageable);

        return exhibitionsPagingResult.map(ExhibitionInfoResponse::new);
    }

    private ExhibitionCustomCondition validateCondition(ExhibitionCustomConditionRequest exhibitionCustomConditionRequest, boolean includeEnd) {
        List<Area> requestedAreas = exhibitionCustomConditionRequest.getAreas();
        List<Month> requestedMonths = exhibitionCustomConditionRequest.getMonths();
        List<Genre> requestedGenres = exhibitionCustomConditionRequest.getGenres();

        if (requestedAreas.isEmpty() || requestedMonths.isEmpty() || requestedGenres.isEmpty()) {
            throw new InvalidRequestException(INVALID_INPUT_VALUE);
        }

        if (requestedAreas.contains(null) || requestedMonths.contains(null) || requestedGenres.contains(null)) {
            throw new InvalidRequestException(INVALID_CUSTOM_EXHB_CONDITION);
        }

        Set<Area> areas = new HashSet<>();
        if (requestedAreas.contains(Area.ALL)) {
            areas.add(Area.ALL);
        } else {
            areas.addAll(requestedAreas);
        }

        Set<Month> months = new HashSet<>();
        if (requestedMonths.contains(Month.ALL)) {
            months.add(Month.ALL);
        } else {
            months.addAll(requestedMonths);
        }

        Set<Genre> genres = new HashSet<>();
        if (requestedGenres.contains(Genre.ALL)) {
            genres.add(Genre.ALL);
        } else {
            genres.addAll(requestedGenres);
        }

        return ExhibitionCustomCondition.builder()
                .areas(areas)
                .months(months)
                .genres(genres)
                .includeEnd(includeEnd)
                .build();
    }

}
