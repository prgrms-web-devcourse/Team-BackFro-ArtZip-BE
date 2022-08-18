package com.prgrms.artzip.comment.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.comment.dto.projection.CommentSimpleProjection;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class CommentResponseQ {

  private final Long commentId;
  private final String content;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
  private final Boolean isEdited;
  private final Boolean isDeleted;
  private final CommentUser user;
  private final Long likeCount;
  private final Boolean isLiked;
  private final Long childrenCount;

  public CommentResponseQ(CommentSimpleProjection projection) {
    this.commentId = projection.getCommentId();
    this.content = projection.getContent();
    this.createdAt = projection.getCreatedAt();
    this.updatedAt = projection.getUpdatedAt();
    this.isEdited = projection.getCreatedAt().isEqual(projection.getUpdatedAt());
    this.isDeleted = projection.getIsDeleted();
    this.user = new CommentUser(projection.getUser());
    this.likeCount = projection.getLikeCount();
    this.isLiked = projection.getIsLiked();
    this.childrenCount = projection.getChildCount();
  }
}
