package com.prgrms.artzip.review.dto.response;

import com.prgrms.artzip.comment.dto.response.CommentInfo;
import com.prgrms.artzip.common.PageResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ReviewCommentInfo {

  private Long commentCount;
  private PageResponse<CommentInfo> comments;
}
