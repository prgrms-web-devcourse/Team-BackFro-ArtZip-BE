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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    private User testUser;
    private Role userRole;
    @Spy
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String testPassword = "test1234!";
    private static final String testEmail = "test@gmail.com";
    private static final String testNickname = "testUser";

    @BeforeEach
    void setUp() {
        userRole = new Role(Authority.USER);
        testUser = LocalUser.builder()
                .email(testEmail)
                .nickname(testNickname)
                .password(passwordEncoder.encode(testPassword))
                .roles(List.of(userRole)).build();
    }

    @Test
    @DisplayName("존재하는 유저 반환 테스트")
    void testGetUserById() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        // when
        User userResult = utilService.getUserById(1L);
        // then
        assertThat(userResult.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 유저 예외 테스트")
    void testGetAnonymousById() {
        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when then
        assertThatThrownBy(() -> utilService.getUserById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }
}