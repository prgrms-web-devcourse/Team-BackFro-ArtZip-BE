package com.prgrms.artzip.comment.dto.response;

import com.prgrms.artzip.comment.domain.Comment;
import java.time.LocalDateTime;
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

  protected CommentInfo(Comment entity) {
    this.isDeleted = entity.getIsDeleted();
    this.content = isDeleted ? null : entity.getContent();
    this.updatedAt = isDeleted ? null : entity.getUpdatedAt();
    this.isEdited = isDeleted ? null : updatedAt != null;
    this.user = isDeleted ? null : new CommentUser(entity.getUser());
    this.commentId = entity.getId();
    this.createdAt = entity.getCreatedAt();
  }
}
