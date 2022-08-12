package com.prgrms.artzip.review.domain.repository;

import static com.prgrms.artzip.comment.domain.QComment.comment;
import static com.prgrms.artzip.review.domain.QReview.review;
import static com.prgrms.artzip.review.domain.QReviewLike.reviewLike;
import static com.prgrms.artzip.review.domain.repository.QuerydslUtils.alwaysFalse;
import static com.prgrms.artzip.review.domain.repository.QuerydslUtils.nullSafeBooleanBuilder;

import com.prgrms.artzip.review.domain.QReviewLike;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

  private final JPAQueryFactory queryFactory;

  private final QReviewLike reviewLikeToGetIsLiked = new QReviewLike("reviewLikeToGetIsLiked");

  public ReviewCustomRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Optional<ReviewWithLikeData> findByReviewIdAndUserId(Long reviewId, Long userId) {

    ReviewWithLikeData data = queryFactory
        .select(Projections.fields(ReviewWithLikeData.class,
            review.id.as("reviewId"),
            review.date,
            review.title,
            review.content,
            review.createdAt,
            review.updatedAt,
            new CaseBuilder()
                .when(alwaysFalse().or(reviewLikeUserIdEq(userId)))
                .then(true)
                .otherwise(false).as("isLiked"),
            review.isPublic,
            reviewLike.id.count().as("likeCount")
        ))
        .from(review)
        .leftJoin(reviewLikeToGetIsLiked).on(reviewLikeToGetIsLiked.review.eq(review),
            alwaysFalse().or(reviewLikeUserIdEq(userId)))
        .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id))
        .where(review.isDeleted.eq(false),
            review.id.eq(reviewId),
            filterIsNotPublic(userId))
        .groupBy(review.id)
        .fetchOne();

    return Optional.ofNullable(data);
  }

  @Override
  public Page<ReviewWithLikeAndCommentCount> findReviewsByExhibitionIdAndUserId(
      Long exhibitionId, Long userId, Pageable pageable) {

    List<ReviewWithLikeAndCommentCount> content = queryFactory.select(
            Projections.fields(ReviewWithLikeAndCommentCount.class,
                review.id.as("reviewId"),
                review.date,
                review.title,
                review.content,
                review.createdAt,
                review.updatedAt,
                new CaseBuilder()
                    .when(alwaysFalse().or(reviewLikeUserIdEq(userId)))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                review.isPublic,
                reviewLike.id.countDistinct().as("likeCount"),
                comment.id.countDistinct().as("commentCount")
            ))
        .from(review)
        .leftJoin(reviewLikeToGetIsLiked).on(reviewLikeToGetIsLiked.review.eq(review),
            alwaysFalse().or(reviewLikeUserIdEq(userId)))
        .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id))
        .leftJoin(comment).on(review.id.eq(comment.review.id))
        .where(review.isDeleted.eq(false),
            review.isPublic.eq(true),
            reviewExhibitionIdEq(exhibitionId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(review.id, review.createdAt)
        .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(review.count())
        .from(review)
        .where(review.isDeleted.eq(false),
            review.isPublic.eq(true),
            reviewExhibitionIdEq(exhibitionId));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ReviewWithLikeAndCommentCount> findMyLikesReviews(
      Long currentUserId, Long targetUserId, Pageable pageable) {

    List<ReviewWithLikeAndCommentCount> content = queryFactory.select(
            Projections.fields(ReviewWithLikeAndCommentCount.class,
                review.id.as("reviewId"),
                review.date,
                review.title,
                review.content,
                review.createdAt,
                review.updatedAt,
                new CaseBuilder()
                    .when(alwaysFalse().or(reviewLikeUserIdEq(currentUserId)))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                review.isPublic,
                reviewLike.id.countDistinct().as("likeCount"),
                comment.id.countDistinct().as("commentCount")
            ))
        .from(review)
        .leftJoin(reviewLikeToGetIsLiked).on(reviewLikeToGetIsLiked.review.eq(review),
            alwaysFalse().or(reviewLikeUserIdEq(currentUserId)))
        .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id))
        .leftJoin(comment).on(review.id.eq(comment.review.id))
        .where(review.isDeleted.eq(false),
            review.isPublic.eq(true),
            reviewLikeTargetUserIdEq(targetUserId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(review.id, review.createdAt)
        .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(review.count())
        .from(review)
        .where(review.isDeleted.eq(false),
            review.isPublic.eq(true),
            reviewLikeTargetUserIdEq(targetUserId));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ReviewWithLikeAndCommentCount> findMyReviews(Long currentUserId, Long targetUserId,
      Pageable pageable) {

    List<ReviewWithLikeAndCommentCount> content = queryFactory.select(
            Projections.fields(ReviewWithLikeAndCommentCount.class,
                review.id.as("reviewId"),
                review.date,
                review.title,
                review.content,
                review.createdAt,
                review.updatedAt,
                new CaseBuilder()
                    .when(alwaysFalse().or(reviewLikeUserIdEq(currentUserId)))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                review.isPublic,
                reviewLike.id.countDistinct().as("likeCount"),
                comment.id.countDistinct().as("commentCount")
            ))
        .from(review)
        .leftJoin(reviewLikeToGetIsLiked).on(reviewLikeToGetIsLiked.review.eq(review),
            alwaysFalse().or(reviewLikeUserIdEq(currentUserId)))
        .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id))
        .leftJoin(comment).on(review.id.eq(comment.review.id), comment.isDeleted.isFalse())
        .where(review.isDeleted.isFalse(),
            review.isPublic.isTrue(),
            reviewTargetUserIdEq(targetUserId),
            filterIsNotPublic(currentUserId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(review.id, review.createdAt)
        .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new))
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(review.count())
        .from(review)
        .where(review.isDeleted.isFalse(),
            review.isPublic.isTrue(),
            reviewTargetUserIdEq(targetUserId),
            filterIsNotPublic(currentUserId));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanBuilder reviewTargetUserIdEq(Long targetUserId) {
    return nullSafeBooleanBuilder(() -> review.user.id.eq(targetUserId));
  }

  private BooleanBuilder reviewLikeTargetUserIdEq(Long targetUserId) {
    return nullSafeBooleanBuilder(() -> reviewLike.user.id.eq(targetUserId));
  }

  private BooleanBuilder reviewExhibitionIdEq(Long exhibitionId) {
    return nullSafeBooleanBuilder(() -> review.exhibition.id.eq(exhibitionId));
  }

  private BooleanBuilder reviewLikeUserIdEq(Long userId) {
    return nullSafeBooleanBuilder(() -> reviewLikeToGetIsLiked.user.id.eq(userId));
  }

  private BooleanExpression filterIsNotPublic(Long userId) {
    if (Objects.isNull(userId)) {
      return review.isPublic.eq(true);
    } else {
      return review.user.id.eq(userId).or(review.user.id.ne(userId).and(review.isPublic.eq(true)));
    }
  }

  private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
    if (pageable.getSort().isEmpty()) {
      return Collections.emptyList();
    }

    return pageable.getSort().stream()
        .filter(order -> ReviewSortType.getReviewSortType(order.getProperty()).isPresent())
        .map(order -> ReviewSortType.getReviewSortType(order.getProperty()).get()
            .getOrderSpecifier(order.getDirection().isAscending() ? Order.ASC : Order.DESC))
        .collect(Collectors.toList());
  }

}
