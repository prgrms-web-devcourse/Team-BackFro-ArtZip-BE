package com.prgrms.artzip.comment.dto.projection;

import com.prgrms.artzip.user.domain.User;
import java.time.LocalDateTime;
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
}
