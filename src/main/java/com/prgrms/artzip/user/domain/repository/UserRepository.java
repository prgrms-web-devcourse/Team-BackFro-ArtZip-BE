package com.prgrms.artzip.user.domain.repository;

import com.prgrms.artzip.user.domain.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u join fetch u.roles r where u.nickname = :nickname and u.isQuit = :isQuit")
    Optional<User> findByNicknameAndIsQuit(@Param("nickname") String nickname, @Param("isQuit") Boolean isQuit);

    @Query("select u from User u join fetch u.roles r where u.id = :userId and u.isQuit = :isQuit")
    Optional<User> findByIdAndIsQuit(@Param("userId") Long userId, @Param("isQuit") Boolean isQuit);

    @Query("select u from User u join fetch u.roles r where u.email = :email and u.isQuit = :isQuit")
    Optional<User> findByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

    boolean existsByEmailAndIsQuit(@Param("email") String email, @Param("isQuit") Boolean isQuit);

    boolean existsByNicknameAndIsQuit(@Param("nickname") String nickname, @Param("isQuit") Boolean isQuit);
    // TODO: exists refactor
    @Query("select case when count(u)> 0 then true else false end from User u where u.id <> :userId and u.nickname = :nickname and u.isQuit = false")
    boolean existsByNicknameExceptId(@Param("userId") Long userId, @Param("nickname") String nickname);
}
