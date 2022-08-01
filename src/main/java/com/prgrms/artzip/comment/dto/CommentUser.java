package com.prgrms.artzip.comment.dto;

import com.prgrms.artzip.user.domain.User;
import lombok.Getter;

@Getter
public class CommentUserResponse {
  private final Long userId;
  private final String nickname;
  private final String profileImage;

  public CommentUserResponse(User user) {
    this.userId = user.getId();
    this.nickname = user.getNickname();
    this.profileImage = user.getProfileImage();
  }
}
