package com.prgrms.artzip.user.domain.repository;

import com.prgrms.artzip.QueryDslTestConfig;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.OAuthUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.transaction.Transactional;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({QueryDslTestConfig.class})
class UserRepositoryTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    private Role userRole = new Role(Authority.USER);

    private Role adminRole = new Role(Authority.ADMIN);

    @BeforeEach
    void setUp() {
        roleRepository.save(userRole);
        roleRepository.save(adminRole);
    }

    @Test
    @DisplayName("user entity 생성 테스트")
    @Transactional
    void testUserCreation() {
        User localUser = LocalUser.builder()
                .email("test@gmail.com")
                .nickname("공공")
                .roles(List.of(userRole))
                .password("test1234!")
                .build();
        User oauthUser = OAuthUser.builder()
                .email("test2@gmail.com")
                .nickname("공공2")
                .roles(List.of(userRole, adminRole))
                .provider("kakao")
                .providerId("kakaoId")
                .build();

        userRepository.save(localUser);
        userRepository.save(oauthUser);

        User userResult = userRepository.findByNicknameAndIsQuit("공공", false).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        User adminResult = userRepository.findByNicknameAndIsQuit("공공2", false).orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        assertThat(userResult.getNickname()).isEqualTo(localUser.getNickname());
        assertThat(userResult.getRoles()).contains(userRole);
        assertThat(adminResult.getNickname()).isEqualTo(oauthUser.getNickname());
        assertThat(oauthUser.getRoles()).containsExactly(userRole, adminRole);
    }
}