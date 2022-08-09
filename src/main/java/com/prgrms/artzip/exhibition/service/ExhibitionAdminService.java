package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ExhibitionAdminService {

  private final ExhibitionRepository exhibitionRepository;

  public Long createExhibition(ExhibitionCreateRequest request) {
    Exhibition exhibition = exhibitionRepository.save(Exhibition.builder()
        .area(request.area())
        .description(request.description())
        .endDate(request.endDate())
        .startDate(request.startDate())
        .genre(request.genre())
        .name(request.name())
        .thumbnail(request.thumbnail())
        .inquiry(request.inquiry())
        .url(request.url())
        .address(request.address())
        .fee(request.fee())
        .latitude(request.latitude())
        .longitude(request.longitude())
        .placeUrl(request.placeUrl())
        .place(request.place())
        .build());
    return exhibition.getId();
  }
}
