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
    this.commentId = entity.getId();
    this.content = entity.getContent();
    this.createdAt = entity.getCreatedAt();
    this.updatedAt = entity.getUpdatedAt();
    this.isEdited = updatedAt != null;
    this.isDeleted = entity.getIsDeleted();
    this.user = new CommentUser(entity.getUser());
  }
}
