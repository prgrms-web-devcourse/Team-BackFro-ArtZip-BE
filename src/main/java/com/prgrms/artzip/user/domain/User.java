package com.prgrms.artzip.user.domain;

import static com.prgrms.artzip.common.ErrorCode.*;
import static org.springframework.util.StringUtils.*;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.prgrms.artzip.common.entity.BaseEntity;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn
@Getter
public class User extends BaseEntity {

  private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  private static final String NICKNAME_REGEX = "[a-zA-Z가-힣0-9]+( [a-zA-Z가-힣0-9]+)*";
  private static final int MAX_EMAIL_LENGTH = 100;
  private static final int MAX_NICKNAME_LENGTH = 10;
  private static final int MAX_PROFILEIMAGE_LENGTH = 300;

  // TODO: max 값 erd 보고 확인
  // TODO: 기본 프사 이미지 s3에 올리고 링크 디폴트로 설정.

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "email", nullable = false, length = MAX_EMAIL_LENGTH)
  private String email;

  @Column(name = "profile_image", length = 300)
  private String profileImage = "https://devcourse-backfro-s3.s3.ap-northeast-2.amazonaws.com/profileImage/default/anonymous-user.jpg";

  @Column(name = "nickname", nullable = false, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @ManyToMany
  @JoinTable(
      name = "user_role",
      joinColumns = @JoinColumn(
          name = "user_id"),
      inverseJoinColumns = @JoinColumn(
          name = "role_id")
  )
  private List<Role> roles = new ArrayList<>();

  @Column(name = "is_quit")
  private Boolean isQuit = false;

  public User(String email, String nickname, List<Role> roles) {
    if (!hasText(email)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    if (!hasText(nickname)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }

    validateEmail(email);
    validateNickname(nickname);

    this.roles = roles;
    this.email = email;
    this.nickname = nickname;
  }

  public void changeQuitFlag(Boolean flag) {
    this.isQuit = flag;
  }

  public void addRole(Role role) {
    roles.add(role);
  }

  public void setNickname(String nickname) {
    if (!hasText(nickname)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    validateNickname(nickname);
    this.nickname = nickname;
  }

  public void setProfileImage(String profileImage) {
    if (!hasText(profileImage)) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    validateProfileImage(profileImage);
    this.profileImage = profileImage;
  }

  private static void validateNickname(String nickname) {
    if (nickname.length() > MAX_NICKNAME_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if (!Pattern.matches(NICKNAME_REGEX, nickname)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateEmail(String email) {
    if (email.length() > MAX_EMAIL_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if (!Pattern.matches(EMAIL_REGEX, email)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateProfileImage(String profileImage) {
    if (profileImage.length() > MAX_PROFILEIMAGE_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
  }
}

