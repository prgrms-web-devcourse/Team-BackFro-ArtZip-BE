package com.prgrms.artzip.user.domain.repository;

import com.prgrms.artzip.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNicknameAndIsQuit(String nickname, Boolean isQuit);

    Optional<User> findByIdAndIsQuit(Long id, Boolean isQuit);

    Optional<User> findByEmailAndIsQuit(String email, Boolean isQuit);

    boolean existsByEmailAndIsQuit(String email, Boolean isQuit);

    boolean existsByNicknameAndIsQuit(String nickname, Boolean isQuit);
}
