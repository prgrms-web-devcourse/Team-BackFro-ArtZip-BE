package com.prgrms.artzip.user.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static com.prgrms.artzip.common.ErrorCode.*;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserUtilServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserUtilService utilService;
  private static Role userRole = new Role(Authority.USER);
  ;
  @Spy
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  private static final String testPassword = "test1234!";
  private static final String testEmail = "test@gmail.com";
  private static final String testNickname = "testUser";

  private User testUser = LocalUser.builder()
      .email(testEmail)
      .nickname(testNickname)
      .password(passwordEncoder.encode(testPassword))
      .roles(List.of(userRole)).build();

  @Test
  @DisplayName("존재하는 유저 반환 테스트")
  void testGetUserById() {
    // given
    when(userRepository.findByIdAndIsQuit(1L, false)).thenReturn(Optional.of(testUser));
    // when
    User userResult = utilService.getUserById(1L);
    // then
    assertThat(userResult).hasFieldOrPropertyWithValue("email", testUser.getEmail());
  }

  @Test
  @DisplayName("존재하지 않는 유저 예외 테스트")
  void testGetAnonymous() {
    // given
    when(userRepository.findByIdAndIsQuit(1L, false)).thenReturn(Optional.empty());
    // when then
    assertThatThrownBy(() -> utilService.getUserById(1L))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(USER_NOT_FOUND.getMessage());
  }

  @ParameterizedTest
  @DisplayName("닉네임 중복 확인")
  @MethodSource("booleanParameter")
  void testCheckNicknameUnique(Boolean testFlag) {
    // given
    when(userRepository.existsByNicknameAndIsQuit(testNickname, false)).thenReturn(testFlag);
    // when
    boolean result = utilService.checkNicknameUnique(testNickname);
    // then
    assertThat(result).isEqualTo(!testFlag);
    verify(userRepository).existsByNicknameAndIsQuit(testNickname, false);
  }

  @ParameterizedTest
  @DisplayName("이메일 중복 확인")
  @MethodSource("booleanParameter")
  void testCheckEmailUnique(Boolean testFlag) {
    // given
    when(userRepository.existsByEmailAndIsQuit(testEmail, false)).thenReturn(testFlag);
    // when
    boolean result = utilService.checkEmailUnique(testEmail);
    // then
    assertThat(result).isEqualTo(!testFlag);
    verify(userRepository).existsByEmailAndIsQuit(testEmail, false);
  }

  private static Stream<Boolean> booleanParameter() {
    return Stream.of(true, false);
  }
}