package com.prgrms.artzip.comment.repository;

import static com.prgrms.artzip.comment.domain.QComment.comment;
import static com.prgrms.artzip.comment.domain.QCommentLike.commentLike;
import static com.prgrms.artzip.common.util.QueryDslCustomUtils.alwaysFalse;
import static com.prgrms.artzip.common.util.QueryDslCustomUtils.nullSafeBooleanBuilder;
import static com.prgrms.artzip.user.domain.QUser.user;

import com.prgrms.artzip.comment.domain.QComment;
import com.prgrms.artzip.comment.domain.QCommentLike;
import com.prgrms.artzip.comment.dto.projection.CommentSimpleProjection;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

  private final JPAQueryFactory queryFactory;

  private final QCommentLike commentLikeToGetIsLiked = new QCommentLike("commentLikeToGetIsLiked");

  private final QComment commentToGetChildren = new QComment("commentToGetChildren");

  @Override
  public Page<CommentSimpleProjection> getCommentsByReviewIdQ(Long reviewId, Long userId,
      Pageable pageable) {
    List<CommentSimpleProjection> comments = queryFactory.select(
            Projections.fields(CommentSimpleProjection.class,
                comment.id.as("commentId"),
                comment.content,
                comment.createdAt,
                comment.updatedAt,
                comment.isDeleted,
                comment.user,
                commentLike.id.countDistinct().as("likeCount"),
                commentToGetChildren.id.countDistinct().as("childCount"),
                new CaseBuilder()
                    .when(alwaysFalse().or(commentLikeEqToUserId(userId)))
                    .then(true)
                    .otherwise(false)
                    .as("isLiked"))
        ).from(comment)
        .leftJoin(comment.user, user)
        .leftJoin(commentLike).on(commentLike.comment.id.eq(comment.id))
        .leftJoin(commentToGetChildren).on(commentToGetChildren.parent.id.eq(comment.id)
            .and(commentToGetChildren.isDeleted.isFalse()))
        .leftJoin(commentLikeToGetIsLiked)
        .on(commentLikeToGetIsLiked.comment.id.eq(comment.id),
            alwaysFalse().or(commentLikeEqToUserId(userId)))
        .where(comment.review.id.eq(reviewId).and(comment.parent.isNull()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(comment.id)
        .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(comment.count())
        .from(comment)
        .where(comment.review.id.eq(reviewId).and(comment.parent.isNull()));

    return PageableExecutionUtils.getPage(comments, pageable, countQuery::fetchOne);
  }


  private BooleanBuilder commentLikeEqToUserId(Long userId) {
    return nullSafeBooleanBuilder(() -> commentLikeToGetIsLiked.user.id.eq(userId));
  }

  private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
    if (pageable.getSort().isEmpty()) {
      return Collections.emptyList();
    }

    return pageable.getSort().stream()
        .map(order -> CommentSortType.getCommentSortType(order.getProperty())
            .getOrderSpecifier(order.getDirection().isAscending() ? Order.ASC : Order.DESC))
        .collect(Collectors.toList());
  }
}
