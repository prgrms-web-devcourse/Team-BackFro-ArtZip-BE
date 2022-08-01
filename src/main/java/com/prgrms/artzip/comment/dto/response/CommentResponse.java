package com.prgrms.artzip.comment.dto.response;

import com.prgrms.artzip.comment.domain.Comment;
import java.util.List;
import lombok.Getter;

@Getter
public class CommentResponse extends CommentInfo {

  private final List<CommentInfo> children;

  public CommentResponse(Comment entity, List<Comment> children) {
    super(entity);
    this.children = children.stream().map(CommentInfo::new).toList();
  }
}
