package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AWSException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Remover;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionCreateOrUpdateRequest;
import com.prgrms.artzip.exhibition.dto.request.ExhibitionSemiUpdateRequest;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionDetailInfoResponse;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static com.prgrms.artzip.common.ErrorCode.AMAZON_S3_ERROR;

@Transactional
@RequiredArgsConstructor
@Service
public class ExhibitionAdminService {

    private final ExhibitionRepository exhibitionRepository;
    private final AmazonS3Uploader amazonS3Uploader;
    private final AmazonS3Remover amazonS3Remover;

    private final String s3DirName = "exhibition";

    public Long createExhibition(ExhibitionCreateOrUpdateRequest request, MultipartFile thumbnail) {
        try {
            String thumbnailAddress = amazonS3Uploader.upload(thumbnail, s3DirName);
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
        Page<ExhibitionForSimpleQuery> exhibitions = exhibitionRepository.findExhibitionsByAdmin(
                pageable);
        return exhibitions.map(ExhibitionInfoResponse::new);
    }

    public ExhibitionDetailInfoResponse getExhibitionDetail(Long exhibitionId) {
        ExhibitionDetailForSimpleQuery exhibition = exhibitionRepository
                .findExhibition(null, exhibitionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXHB_NOT_FOUND));

        return new ExhibitionDetailInfoResponse(exhibition, new ArrayList<>());
    }

    public void updateExhibition(Long exhibitionId, ExhibitionCreateOrUpdateRequest request, MultipartFile thumbnail) {
        Exhibition exhibition = getExhibition(exhibitionId);
        exhibition.update(request);
        if (Objects.nonNull(thumbnail)) {
            try {
                String prevThumbnailPath = exhibition.getThumbnail();
                String newThumbnailPath = amazonS3Uploader.upload(thumbnail, s3DirName);
                exhibition.changeThumbnail(newThumbnailPath);
                amazonS3Remover.removeFile(prevThumbnailPath, s3DirName);
            } catch (IOException e) {
                throw new AWSException(AMAZON_S3_ERROR);
            }
        }
    }

    public void semiUpdateExhibition(Long exhibitionId, ExhibitionSemiUpdateRequest request) {
        Exhibition exhibition = getExhibition(exhibitionId);
        exhibition.updateGenreAndDescription(request);
    }

    public void deleteExhibition(Long exhibitionId) {
        Exhibition exhibition = getExhibition(exhibitionId);
        exhibition.deleteExhibition();
    }

    private Exhibition getExhibition(Long exhibitionId) {
        return exhibitionRepository
                .findById(exhibitionId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXHB_NOT_FOUND));
    }
}
