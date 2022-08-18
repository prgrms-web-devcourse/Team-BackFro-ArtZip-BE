package com.prgrms.artzip.comment.dto.projection;

import com.prgrms.artzip.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentSimpleProjection {
  private Long commentId;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private Boolean isDeleted;
  private User user;
  private Long likeCount;
  private Long childCount;
  private Boolean isLiked;

  @Builder
  public CommentSimpleProjection(Long commentId, String content, LocalDateTime createdAt,
      LocalDateTime updatedAt, Boolean isDeleted, User user, Long likeCount,
      Long childCount, Boolean isLiked) {
    this.commentId = commentId;
    this.content = content;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
    this.isDeleted = isDeleted;
    this.user = user;
    this.likeCount = likeCount;
    this.childCount = childCount;
    this.isLiked = isLiked;
  }
}
