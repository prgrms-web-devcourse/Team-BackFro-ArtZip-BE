package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.AMAZON_S3_ERROR;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AWSException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepositoryImpl;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionDetailInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import com.prgrms.artzip.review.service.ReviewService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
  private final ReviewService reviewService;

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
    Page<ExhibitionForSimpleQuery> exhibitions = exhibitionRepositoryImpl.findExhibitionsByAdmin(
        pageable);
    return exhibitions.map(ExhibitionInfoResponse::new);
  }

  public ExhibitionDetailInfoResponse getExhibition(Long exhibitionId) {
    ExhibitionDetailForSimpleQuery exhibition = exhibitionRepositoryImpl
        .findExhibition(null, exhibitionId)
        .orElseThrow(() -> {
          throw new NotFoundException(ErrorCode.EXHB_NOT_FOUND);
        });

    return ExhibitionDetailInfoResponse.builder()
        .exhibitionId(exhibition.getId())
        .name(exhibition.getName())
        .thumbnail(exhibition.getThumbnail())
        .startDate(exhibition.getPeriod().getStartDate())
        .endDate(exhibition.getPeriod().getEndDate())
        .area(exhibition.getLocation().getArea())
        .url(hasText(exhibition.getUrl()) ? exhibition.getUrl() : null)
        .placeUrl(hasText(exhibition.getPlaceUrl()) ? exhibition.getPlaceUrl() : null)
        .inquiry(exhibition.getInquiry())
        .genre(exhibition.getGenre())
        .description(exhibition.getDescription())
        .likeCount(exhibition.getLikeCount())
        .placeAddress(exhibition.getLocation().getAddress())
        .lat(exhibition.getLocation().getLatitude())
        .lng(exhibition.getLocation().getLongitude())
        .isLiked(exhibition.getIsLiked())
        .reviews(new ArrayList<>())
        .build();
  }
}
