package com.prgrms.artzip.review.domain.repository;

import static com.prgrms.artzip.comment.domain.QComment.comment;
import static com.prgrms.artzip.review.domain.QReview.review;
import static com.prgrms.artzip.review.domain.QReviewLike.reviewLike;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import java.util.Arrays;
import java.util.Optional;

public enum ReviewSortType {

  CREATED_AT("createdAt", review.createdAt),
  REVIEW_LIKE_COUNT("reviewLikeCount", reviewLike.id.countDistinct()),
  COMMENT_COUNT("commentCount", comment.id.countDistinct());

  private final String property;
  private final Expression target;

  ReviewSortType(String property, Expression target) {
    this.property = property;
    this.target = target;
  }

  public OrderSpecifier<?> getOrderSpecifier(Order direction) {
    return new OrderSpecifier(direction, this.target);
  }

  public static Optional<ReviewSortType> getReviewSortType(String property) {
    return Optional.ofNullable(
        Arrays.stream(ReviewSortType.values())
            .filter(reviewSortType -> reviewSortType.property.equals(property))
            .findAny()
            .orElse(null));
  }

}
