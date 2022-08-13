package com.prgrms.artzip.comment.repository;

import static com.prgrms.artzip.comment.domain.QComment.comment;
import static com.prgrms.artzip.comment.domain.QCommentLike.commentLike;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommentSortType {
  CREATED_AT("createdAt", comment.createdAt),
  COMMENT_ID("id", comment.id),
  LIKE_COUNT("likeCount", commentLike.commentLikeId.countDistinct());

  private final String property;
  private final Expression target;

  public OrderSpecifier<?> getOrderSpecifier(Order direction) {
    return new OrderSpecifier(direction, this.target);
  }

  public static CommentSortType getCommentSortType(String property) {
    return Arrays.stream(CommentSortType.values())
        .filter(commentSortType -> commentSortType.property.equals(property))
        .findAny().orElseThrow(() -> {throw new NotFoundException(ErrorCode.INVALID_COMMENT_SORT_TYPE);});
  }
}
