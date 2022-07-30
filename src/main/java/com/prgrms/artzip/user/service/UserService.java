package com.prgrms.artzip.user.service;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.UserRegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Transactional
    public User login(String principal, String credentials){
        if(!hasText(principal)) throw new InvalidRequestException(PRINCIPAL_REQUIRED);
        if(!hasText(credentials)) throw new InvalidRequestException(CREDENTIAL_REQUIRED);

        LocalUser user = (LocalUser) userRepository.findByEmailAndIsQuit(principal, false).orElseThrow(() -> new NotFoundException(USER_NOT_FOUND));
        user.checkPassword(passwordEncoder, credentials);
        return user;
    }

    @Transactional
    public User register(UserRegisterRequest request) {
        if(userRepository.existsByEmailAndIsQuit(request.getEmail(), false)) throw new AlreadyExistsException(USER_ALREADY_EXISTS);
        if(userRepository.existsByNicknameAndIsQuit(request.getNickname(), false)) throw new AlreadyExistsException(USER_ALREADY_EXISTS);

        User newUser = LocalUser.builder()
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return userRepository.save(newUser);
    }


}
