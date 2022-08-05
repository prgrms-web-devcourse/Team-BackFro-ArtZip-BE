package com.prgrms.artzip.user.service;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.RoleRepository;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public User login(String principal, String credentials){
        if(!hasText(principal) || !hasText(credentials)) throw new InvalidRequestException(LOGIN_PARAM_REQUIRED);

        LocalUser user = (LocalUser) userRepository.findByEmailAndIsQuit(principal, false).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        user.checkPassword(passwordEncoder, credentials);
        return user;
    }

    @Transactional
    public User signUp(UserSignUpRequest request) {
        if (userRepository.existsByEmailAndIsQuit(request.getEmail(), false))
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        if (userRepository.existsByNicknameAndIsQuit(request.getNickname(), false))
            throw new AlreadyExistsException(USER_ALREADY_EXISTS);

        Role userRole = roleRepository.findByAuthority(Authority.USER).orElseThrow(() -> new NotFoundException(ROLE_NOT_FOUND));
        User newUser = LocalUser.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(List.of(userRole))
                .build();
        return userRepository.save(newUser);
    }

}
