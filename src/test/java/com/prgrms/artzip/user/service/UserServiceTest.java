package com.prgrms.artzip.user.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;
import static com.prgrms.artzip.common.ErrorCode.*;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.RoleRepository;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserRegisterRequest;
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
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Role userRole;

    private User newUser;

    private static final String testPassword = "test1234!";

    @BeforeEach
    void setUp() {
        userRole = new Role(Authority.USER);
        newUser = LocalUser.builder()
                .email("test@gmail.com")
                .nickname("testUser")
                .password(passwordEncoder.encode(testPassword))
                .roles(List.of(userRole)).build();
    }

    @Test
    @DisplayName("정상 회원가입 테스트")
    void testRegister() {
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .password(testPassword).build();
        // given
        when(userRepository.existsByEmailAndIsQuit(registerRequest.getEmail(), false)).thenReturn(false);
        when(userRepository.existsByNicknameAndIsQuit(registerRequest.getNickname(), false)).thenReturn(false);
        when(roleRepository.findByAuthority(Authority.USER)).thenReturn(Optional.of(userRole));
        when(userRepository.save(any())).thenReturn(newUser);

        //when
        User userResult = userService.register(registerRequest);

        //then
        assertThat(userResult.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(userResult.getNickname()).isEqualTo(newUser.getNickname());
        assertThat(userResult.getRoles()).containsAll(newUser.getRoles());
        verify(roleRepository).findByAuthority(Authority.USER);
        verify(userRepository).save(any());
    }

    @Test
    @DisplayName("이메일이 이미 존재하는 경우 회원가입 테스트")
    void testEmailExistRegister() {
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .password(testPassword).build();
        // given
        when(userRepository.existsByEmailAndIsQuit(registerRequest.getEmail(), false)).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(USER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("닉네임이 이미 존재하는 경우 회원가입 테스트")
    void testNicknameExistRegister() {
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .password(testPassword).build();
        // given
        when(userRepository.existsByNicknameAndIsQuit(registerRequest.getNickname(), false)).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(AlreadyExistsException.class)
                .hasMessage(USER_ALREADY_EXISTS.getMessage());
    }

    @Test
    @DisplayName("정상 로그인 테스트")
    void testUserLogin() {
        UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(newUser.getEmail(), testPassword);

        // given
        when(userRepository.findByEmailAndIsQuit(localLoginRequest.getEmail(), false)).thenReturn(Optional.of(newUser));

        // when
        User userResult = userService.login(localLoginRequest.getEmail(), localLoginRequest.getPassword());

        // then
        assertThat(userResult.getEmail()).isEqualTo(newUser.getEmail());
        assertThat(userResult.getNickname()).isEqualTo(newUser.getNickname());
        assertThat(userResult.getRoles()).containsAll(newUser.getRoles());
        verify(userRepository).findByEmailAndIsQuit(localLoginRequest.getEmail(), false);
    }

    @Test
    @DisplayName("없는 유저에 대한 로그인 테스트")
    void testAnonymousLogin() {
        // given
        UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(newUser.getEmail(), testPassword);

        // when
        when(userRepository.findByEmailAndIsQuit(localLoginRequest.getEmail(), false)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> userService.login(localLoginRequest.getEmail(), localLoginRequest.getPassword()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(USER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 시 이메일 누락 테스트")
    void testEmailNullParameter() {
        //given
        UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(null, testPassword);

        //when then
        assertThatThrownBy(() -> userService.login(localLoginRequest.getEmail(), localLoginRequest.getPassword()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(LOGIN_PARAM_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("로그인 시 비밀번호 누락 테스트")
    void testPassNullParameter() {
        //given
        UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(newUser.getEmail(), null);

        //when then
        assertThatThrownBy(() -> userService.login(localLoginRequest.getEmail(), localLoginRequest.getPassword()))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage(LOGIN_PARAM_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("회원가입 시 user role 에러 테스트")
    void testAuthorityRegister() {
        //given
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .password(testPassword).build();
        when(roleRepository.findByAuthority(Authority.USER)).thenReturn(Optional.empty());
        //when then
        assertThatThrownBy(() -> userService.register(registerRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(ROLE_NOT_FOUND.getMessage());
    }
}