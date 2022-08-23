package com.prgrms.artzip.exhibition.service;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.ExhibitionLike;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionLikeResponse;
import com.prgrms.artzip.exhibition.repository.ExhibitionLikeRepository;
import com.prgrms.artzip.exhibition.repository.ExhibitionRepository;
import com.prgrms.artzip.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeService {
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionLikeRepository exhibitionLikeRepository;

    @Transactional
    public ExhibitionLikeResponse updateExhibitionLike(User user, Long exhibitionId) {
        Optional<ExhibitionLike> optionalExhibitionLike = exhibitionLikeRepository.findByUserIdAndExhibitionId(user.getId(), exhibitionId);

        boolean isLiked = optionalExhibitionLike.isPresent();
        optionalExhibitionLike.ifPresentOrElse(exhibitionLikeRepository::delete, () -> {
            Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));

            if (exhibition.getIsDeleted()) {
                throw new InvalidRequestException(EXHB_NOT_FOUND);
            }

            exhibitionLikeRepository.save(new ExhibitionLike(user, exhibition));
        });

        Long likeCount = exhibitionLikeRepository.countByExhibitionId(exhibitionId);

        return ExhibitionLikeResponse.builder()
                .exhibitionId(exhibitionId)
                .likeCount(likeCount)
                .isLiked(!isLiked)
                .build();
    }

    @Transactional(readOnly = true)
    public Long getExhibitionLikeCountByUserId(Long userId) {
        return exhibitionLikeRepository.countByUserId(userId);
    }
}
