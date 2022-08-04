package com.prgrms.artzip.review.service;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.review.domain.ReviewPhoto;
import com.prgrms.artzip.review.domain.repository.ReviewPhotoRepository;
import com.prgrms.artzip.review.domain.repository.ReviewRepository;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.review.dto.response.ReviewCreateResponse;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private static final String REVIEW_DIRECTORY_NAME = "review/";
    private static final int REVIEW_PHOTO_COUNT = 9;

    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final AmazonS3Uploader amazonS3Uploader;

    @Transactional
    public ReviewCreateResponse createReview(final Long userId, final ReviewCreateRequest request,
                                             final List<MultipartFile> files) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Exhibition exhibition = exhibitionRepository.findById(request.getExhibitionId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXHB_NOT_FOUND));
        validateFileCount(files);

        Review review = Review.builder()
                .user(user)
                .exhibition(exhibition)
                .content(request.getContent())
                .title(request.getTitle())
                .date(request.getDate())
                .isPublic(request.getIsPublic())
                .build();
        Review savedReview = reviewRepository.save(review);

        if (files != null) {
            createReviewPhoto(savedReview, files);
        }

        return new ReviewCreateResponse(savedReview.getId());
    }

    private void createReviewPhoto(final Review review, final List<MultipartFile> files) {
        files.forEach(file -> {
            validateFileExtension(file);
        });

        files.forEach(file -> {
            try {
                String path = amazonS3Uploader.upload(file, REVIEW_DIRECTORY_NAME + review.getId());
                reviewPhotoRepository.save(new ReviewPhoto(review, path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void validateFileExtension(final MultipartFile file) {
        String filename = file.getOriginalFilename();
        String fileExtension = filename.substring(filename.lastIndexOf("."));
        if (!(fileExtension.equalsIgnoreCase(".jpg") ||
                fileExtension.equalsIgnoreCase(".jpeg") ||
                fileExtension.equalsIgnoreCase(".png"))) {
            throw new InvalidRequestException(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private void validateFileCount(List<MultipartFile> files) {
        if (files != null && files.size() > REVIEW_PHOTO_COUNT) {
            throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_PHOTO_COUNT);
        }
    }

    @Transactional(readOnly = true)
    public Long getReviewCountByUserId(Long userId) {
        return reviewRepository.countByUserId(userId);
    }
}
