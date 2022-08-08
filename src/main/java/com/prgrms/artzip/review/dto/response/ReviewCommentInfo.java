package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentResponse;
import com.prgrms.artzip.common.PageResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Page;

@SuperBuilder
@Getter
public class ReviewCommentInfo {

  private Long commentCount;
  private PageResponse<CommentResponse> comments;

  public ReviewCommentInfo(Long commentCount, Page<CommentResponse> comments) {
    this.commentCount = commentCount;
    this.comments = new PageResponse<>(comments);
  }
}
