package com.prgrms.artzip.user.service;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import com.prgrms.artzip.common.Authority;
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
    @DisplayName("회원가입 테스트")
    void testRegister() {
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .email(newUser.getEmail())
                .nickname(newUser.getNickname())
                .password(testPassword).build();
        // given
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
    @DisplayName("있는 유저에 대한 로그인 테스트")
    void testLogin() {
        UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(newUser.getEmail(), "test1234!");

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
}