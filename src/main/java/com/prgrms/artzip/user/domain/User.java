package com.prgrms.artzip.user.domain;

import static com.prgrms.artzip.common.ErrorCode.*;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import java.util.regex.Pattern;
import org.springframework.util.Assert;
import com.prgrms.artzip.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
  private static final int MAX_NAME_LENGTH = 10;
  private static final int MAX_PROFILEIMAGE_LENGTH = 300;

  // TODO: max 값 erd 보고 확인
  // TODO: 기본 프사 이미지 s3에 올리고 링크 디폴트로 설정.

  @Id
  @Column(name = "user_id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "email", nullable = false, length = MAX_EMAIL_LENGTH)
  private String email;

  @Column(name = "profile_image", length = 300)
  private String profileImage = "default s3 profileImage link";

  @Column(name = "nickname", nullable = false, length = MAX_NICKNAME_LENGTH)
  private String nickname;

  @Column(name = "is_quit")
  private Boolean isQuit = false;

  /*TODO : Refactoring*/

  private static void validateNickname(String nickname) {
    if(nickname.length() > MAX_NICKNAME_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if(!Pattern.matches(NICKNAME_REGEX, nickname)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateEmail(String email) {
    if(email.length() > MAX_EMAIL_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
    if(!Pattern.matches(EMAIL_REGEX, email)) {
      throw new InvalidRequestException(INVALID_INPUT_VALUE);
    }
  }

  private static void validateProfileImage(String profileImage) {
    if(profileImage.length() > MAX_PROFILEIMAGE_LENGTH) {
      throw new InvalidRequestException(INVALID_LENGTH);
    }
  }

  public User(String email, String nickname) {
    Assert.hasText(email, "email이 누락되었습니다.");
    Assert.hasText(nickname, "nickname이 누락되었습니다.");

    validateEmail(email);
    validateNickname(nickname);

    this.email = email;
    this.nickname = nickname;
  }

  public void changeQuitFlag(Boolean flag) {
    this.isQuit = flag;
  }
}

