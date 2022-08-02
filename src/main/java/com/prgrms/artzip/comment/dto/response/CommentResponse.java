package com.prgrms.artzip.comment.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrms.artzip.comment.domain.Comment;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class CommentResponse extends CommentInfo {

  private final List<CommentInfo> children;
  private final Integer childrenCount;

  public CommentResponse(Comment entity, List<Comment> children) {
    super(entity);
    this.childrenCount = children.size();
    this.children = childrenCount < 10 ? children.stream().map(CommentInfo::new).toList() : new ArrayList<>();
  }
}
