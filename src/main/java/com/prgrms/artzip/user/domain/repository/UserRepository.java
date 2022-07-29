package com.prgrms.artzip.user.domain.repository;

import com.prgrms.artzip.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNicknameAndIsQuit(String nickname, Boolean isQuit);
}
