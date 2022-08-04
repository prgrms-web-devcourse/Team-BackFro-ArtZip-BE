package com.prgrms.artzip.exibition.service;

import static com.prgrms.artzip.common.ErrorCode.EXHB_NOT_FOUND;
import static com.prgrms.artzip.common.ErrorCode.USER_NOT_FOUND;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.exibition.domain.ExhibitionLike;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionLikeRepository;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionRepository;
import com.prgrms.artzip.exibition.dto.response.ExhibitionLikeResult;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeService {

  private final ExhibitionRepository exhibitionRepository;
  private final ExhibitionLikeRepository exhibitionLikeRepository;
  private final UserRepository userRepository;

  @Transactional
  public ExhibitionLikeResult updateExhibitionLike(Long userId, Long exhibitionId) {
    // userId가 null인 경우 권한 없음 응답(?)

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));

    Optional<ExhibitionLike> optionalExhibitionLike = exhibitionLikeRepository
        .findByUserIdAndExhibitionId(userId, exhibitionId);

    boolean isLiked = optionalExhibitionLike.isPresent();
    optionalExhibitionLike.ifPresentOrElse(exhibitionLike -> {
      exhibitionLikeRepository.delete(exhibitionLike);
    }, () -> {
      Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
          .orElseThrow(() -> new InvalidRequestException(EXHB_NOT_FOUND));
      exhibitionLikeRepository.save(new ExhibitionLike(user, exhibition));
    });

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
