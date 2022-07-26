package com.prgrms.artzip.comment.dto.response;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.prgrms.artzip.comment.domain.Comment;
import com.prgrms.artzip.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
@JsonInclude(NON_NULL)
public class CommentResponse extends CommentInfo {

  private final Integer childrenCount;

  public CommentResponse(Comment entity, User user, List<Comment> children) {
    super(entity, user);
    this.childrenCount = children.size();
  }
}
