package com.prgrms.artzip.user.domain.repository;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(Authority authority);
}
