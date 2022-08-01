package com.prgrms.artzip.comment.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CommentResponse {
  private final Long commentId;
  private final String content;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;
  private final Boolean isEdited;
  private final Boolean isDeleted;
  private final CommentUserResponse user;
  private final 
}
