package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.AMAZON_S3_ERROR;

import com.prgrms.artzip.common.error.exception.AWSException;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepositoryImpl;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@RequiredArgsConstructor
@Service
public class ExhibitionAdminService {

  private final ExhibitionRepository exhibitionRepository;
  private final AmazonS3Uploader amazonS3Uploader;
  private final ExhibitionRepositoryImpl exhibitionRepositoryImpl;

  public Long createExhibition(ExhibitionCreateRequest request, MultipartFile thumbnail) {
    try {
      String thumbnailAddress = amazonS3Uploader.upload(thumbnail, "exhibition");
      Exhibition exhibition = exhibitionRepository.save(Exhibition.builder()
          .area(request.getArea())
          .description(request.getDescription())
          .endDate(request.getEndDate())
          .startDate(request.getStartDate())
          .genre(request.getGenre())
          .name(request.getName())
          .thumbnail(thumbnailAddress)
          .inquiry(request.getInquiry())
          .url(request.getUrl())
          .address(request.getAddress())
          .fee(request.getFee())
          .latitude(request.getLatitude())
          .longitude(request.getLongitude())
          .placeUrl(request.getPlaceUrl())
          .place(request.getPlace())
          .build());
      return exhibition.getId();
    } catch (IOException ex) {
      throw new AWSException(AMAZON_S3_ERROR);
    }
  }

  public Page<ExhibitionInfoResponse> getExhibitions(Pageable pageable) {
    Page<ExhibitionForSimpleQuery> exhibitions = exhibitionRepositoryImpl.findExhibitionsByAdmin(pageable);
    return exhibitions.map(ExhibitionInfoResponse::new);
  }
}
