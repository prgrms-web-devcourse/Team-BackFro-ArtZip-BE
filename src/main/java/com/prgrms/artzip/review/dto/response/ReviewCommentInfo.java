package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@SuperBuilder
@Getter
public class ReviewCommentInfo {

  private Long commentCount;
  private Page<CommentResponse> comments;

  public ReviewCommentInfo(Long commentCount, Page<CommentResponse> comments) {
    this.commentCount = commentCount;
    this.comments = comments;
  }
}
