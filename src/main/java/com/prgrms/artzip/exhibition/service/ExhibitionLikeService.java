package com.prgrms.artzip.exhibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.Exhibition;
import com.prgrms.artzip.exhibition.domain.ExhibitionLike;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionLikeRepository;
import com.prgrms.artzip.exhibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionLikeResponse;
import com.prgrms.artzip.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeService {

  private final ExhibitionRepository exhibitionRepository;
  private final ExhibitionLikeRepository exhibitionLikeRepository;

  @Transactional
  public ExhibitionLikeResponse updateExhibitionLike(User user, Long exhibitionId) {
    Optional<ExhibitionLike> optionalExhibitionLike = exhibitionLikeRepository
        .findByUserIdAndExhibitionId(user.getId(), exhibitionId);

    boolean isLiked = optionalExhibitionLike.isPresent();
    optionalExhibitionLike.ifPresentOrElse(exhibitionLike -> {
      exhibitionLikeRepository.delete(exhibitionLike);
    }, () -> {
      Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
          .orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));

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
