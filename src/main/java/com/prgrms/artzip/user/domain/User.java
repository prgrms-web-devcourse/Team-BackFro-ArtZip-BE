package com.prgrms.artzip.user.domain;

import static lombok.AccessLevel.PROTECTED;

import com.prgrms.artzip.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
@Getter
public class User extends BaseEntity {
  @Id
  @Column(name = "user_id")
  private Long id;

  @Column(name = "email", nullable = false)
  private String email;

  private String nickname;

  private String phone;

}
