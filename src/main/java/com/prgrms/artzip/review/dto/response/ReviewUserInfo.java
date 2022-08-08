package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.user.domain.User;
import lombok.Getter;

@Getter
public class ReviewUserInfo {
  private Long userId;
  private String nickname;
  private String profileImage;

  public ReviewUserInfo(User user) {
    this.userId = user.getId();
    this.nickname = user.getNickname();
    this.profileImage = user.getProfileImage();
  }
}