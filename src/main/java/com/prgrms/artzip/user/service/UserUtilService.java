package com.prgrms.artzip.user.service;

import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.prgrms.artzip.common.ErrorCode.USER_NOT_FOUND;

@Service
public class UserUtilService {

  private final UserRepository userRepository;

  public UserUtilService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public User getUserById(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public boolean checkNicknameUnique(String nickname) {
    return userRepository.existsByNicknameAndIsQuit(nickname, false);
  }

  @Transactional(readOnly = true)
  public boolean checkEmailUnique(String nickname) {
    return userRepository.existsByEmailAndIsQuit(nickname, false);
  }
}
