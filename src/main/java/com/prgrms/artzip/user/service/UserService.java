package com.prgrms.artzip.user.service;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;
import static java.util.Objects.*;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Remover;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.RoleRepository;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import com.prgrms.artzip.user.dto.request.UserUpdateRequest;
import com.prgrms.artzip.user.dto.response.UserUpdateResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

// TODO: 사진 파일 관련 util 추상화 refactoring
@Service
@RequiredArgsConstructor
public class UserService {
  private Logger log = LoggerFactory.getLogger(getClass());
  private static final String PROFILE_DIRECTORY_NAME = "profileImage";
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final UserUtilService userUtilService;

  private final AmazonS3Uploader amazonS3Uploader;

  private final AmazonS3Remover amazonS3Remover;

  @Transactional(readOnly = true)
  public User login(String principal, String credentials) {
      if (!hasText(principal) || !hasText(credentials)) {
          throw new InvalidRequestException(LOGIN_PARAM_REQUIRED);
      }

    LocalUser user = (LocalUser) userRepository.findByEmailAndIsQuit(principal, false)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
    user.checkPassword(passwordEncoder, credentials);
    return user;
  }

  @Transactional
  public User signUp(UserSignUpRequest request) {
      if (userRepository.existsByEmailAndIsQuit(request.getEmail(), false)) {
          throw new AlreadyExistsException(USER_ALREADY_EXISTS);
      }
      if (userRepository.existsByNicknameAndIsQuit(request.getNickname(), false)) {
          throw new AlreadyExistsException(USER_ALREADY_EXISTS);
      }

    Role userRole = roleRepository.findByAuthority(Authority.USER)
        .orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
    User newUser = LocalUser.builder()
        .nickname(request.getNickname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .roles(List.of(userRole))
        .build();
    return userRepository.save(newUser);
  }
  @Transactional
  public UserUpdateResponse updateUserInfo(User user, UserUpdateRequest request, MultipartFile file) {
    // 닉네임 업데이트
    if (userRepository.existsByNicknameExceptId(user.getId(), request.getNickname())) {
      throw new AlreadyExistsException(NICKNAME_ALREADY_EXISTS);
    }
    user.setNickname(request.getNickname());
    String updatedProfile = amazonS3Uploader.getDefaultProfileImage();
    try {
      // 이미지 업데이트
      if (nonNull(file) && file.getSize() > 0) {
        // 새로운 사진으로 수정하는 경우
        validateFileExtension(file);
        updatedProfile = amazonS3Uploader.upload(file, PROFILE_DIRECTORY_NAME);
      } else {
        // 수정하지 않은 경우
        if (hasText(request.getProfileImage())) {
          if (request.getProfileImage().equals(amazonS3Uploader.getDefaultProfileImage()) || user.getProfileImage().equals(request.getProfileImage()))
            updatedProfile = request.getProfileImage();
          else
            throw new InvalidRequestException(USER_PROFILE_NOT_MATCHED);
        }
      }
      if (!user.getProfileImage().equals(amazonS3Uploader.getDefaultProfileImage()) && !user.getProfileImage().equals(request.getProfileImage()))
        amazonS3Remover.removeFile(user.getProfileImage(), PROFILE_DIRECTORY_NAME);
      user.setProfileImage(updatedProfile);
    } catch (IOException e) {
      log.error("이미지 처리 실패: {}", e.getMessage());
    }
    userRepository.save(user);
    return UserUpdateResponse.builder()
        .userId(user.getId())
        .email(user.getEmail())
        .nickname(user.getNickname())
        .profileImage(user.getProfileImage())
        .build();
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

}
