package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.ExhibitionLikeId;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionLikeRepository;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.response.ExhibitionLikeResult;
import com.prgrms.artzip.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeService {
  private final ExhibitionRepository exhibitionRepository;
  private final ExhibitionLikeRepository exhibitionLikeRepository;

  // 차후 수정 필요!
  @Transactional
  public ExhibitionLikeResult updateExhibitionLike(Long exhibitionId, User user) {
    ExhibitionLikeId exhibitionLikeId = new ExhibitionLikeId(exhibitionId, user.getId());
    boolean isLiked = exhibitionLikeRepository.findById(exhibitionLikeId).isPresent();

    if(isLiked) {
      exhibitionLikeRepository.deleteById(exhibitionLikeId);
    }else {
      Exhibition exhibition = exhibitionRepository.findById(exhibitionId).orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));
      exhibitionLikeRepository.save(new ExhibitionLike(exhibition, user));
    }

    // jpql을 사용하기 때문에 flush
    Long likeCount = exhibitionLikeRepository.countByExhibitionId(exhibitionId);

    return ExhibitionLikeResult.builder()
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
