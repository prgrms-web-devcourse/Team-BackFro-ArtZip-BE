package com.prgrms.artzip.comment.dto.response;

import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

@Getter
public class CommentInfo {
  private final Long commentId;
  private final String content;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
  private final Boolean isEdited;
  private final Boolean isDeleted;
  private final CommentUser user;
  private final Integer likeCount;
  private final Boolean isLiked;

  public CommentInfo(Comment entity, User user) {
    this.isDeleted = entity.getIsDeleted();
    this.content = isDeleted ? null : entity.getContent();
    this.updatedAt = isDeleted ? null : entity.getUpdatedAt();
    this.isEdited = isDeleted ? null : updatedAt != null;
    this.user = isDeleted ? null : new CommentUser(entity.getUser());
    this.commentId = entity.getId();
    this.createdAt = entity.getCreatedAt();
    this.likeCount = entity.getCommentLikes().size();
    if (Objects.nonNull(user)) {
      this.isLiked = entity.getCommentLikes().stream().anyMatch(cl -> Objects.equals(
          cl.getUser().getId(), user.getId()));
    } else{
      this.isLiked = false;
    }
  }
}
