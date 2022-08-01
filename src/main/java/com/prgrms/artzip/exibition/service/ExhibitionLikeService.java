package com.prgrms.artzip.exibition.service;

import com.prgrms.artzip.exibition.domain.ExhibitionLikeId;
import com.prgrms.artzip.exibition.domain.repository.ExhibitionLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitionLikeService {
  private final ExhibitionLikeRepository exhibitionLikeRepository;

  @Transactional(readOnly = true)
  public boolean isLikedExhibition(Long exhibitionId, Long userId) {
    if(exhibitionLikeRepository.findById(new ExhibitionLikeId(exhibitionId, userId)).isPresent()) {
      return true;
    }
    return false;
  }
}
