package com.prgrms.artzip.user.domain.repository;

import com.prgrms.artzip.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u join fetch u.roles us where u.nickname = :nickname and u.isQuit = :isQuit")
    Optional<User> findByNicknameAndIsQuit(String nickname, Boolean isQuit);

    @Query("select u from User u join fetch u.roles us where u.id = :userId and u.isQuit = :isQuit")
    Optional<User> findByIdAndIsQuit(Long userId, Boolean isQuit);

    @Query("select u from User u join fetch u.roles us where u.email = :email and u.isQuit = :isQuit")
    Optional<User> findByEmailAndIsQuit(String email, Boolean isQuit);

    boolean existsByEmailAndIsQuit(String email, Boolean isQuit);

    boolean existsByNicknameAndIsQuit(String nickname, Boolean isQuit);
}
