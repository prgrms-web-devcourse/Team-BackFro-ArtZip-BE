package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.AMAZON_S3_ERROR;

import com.prgrms.artzip.common.error.exception.AWSException;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ExhibitionAdminService {

  private final ExhibitionRepository exhibitionRepository;
  private final AmazonS3Uploader amazonS3Uploader;

  public Long createExhibition(ExhibitionCreateRequest request, MultipartFile thumbnail) {
    try {
      String thumbnailAddress = amazonS3Uploader.upload(thumbnail, "/exhibition");
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
}
