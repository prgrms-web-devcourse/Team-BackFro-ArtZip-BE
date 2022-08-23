package com.prgrms.artzip.review.domain.repository;

import static com.prgrms.artzip.comment.domain.QComment.comment;
import static com.prgrms.artzip.common.util.QueryDslCustomUtils.nullSafeBooleanBuilder;
import static com.prgrms.artzip.common.util.QueryDslCustomUtils.nullSafeConditions;
import static com.prgrms.artzip.review.domain.QReview.review;
import static com.prgrms.artzip.review.domain.QReviewLike.reviewLike;

import com.prgrms.artzip.review.domain.QReviewLike;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeAndCommentCount;
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
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.Assert;

public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

  private static final String REVIEW_ID_MUST_NOT_BE_NULL = "The given review id must not be null!";
  private static final String USER_ID_MUST_NOT_BE_NULL = "The given user id must not be null!";
  private static final String PAGEABLE_MUST_NOT_BE_NULL = "The given pageable must not be null!";

  private final JPAQueryFactory queryFactory;

  // like count 계산할 때 사용
  private final QReviewLike reviewLikeToGetIsLiked = new QReviewLike("reviewLikeToGetIsLiked");
  // target user의 like를 필터링할 때 사용
  private final QReviewLike reviewLikeToFilterTargetUser = new QReviewLike("reviewLikeToFilterTargetUser");

  public ReviewCustomRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Optional<ReviewWithLikeAndCommentCount> findReviewByReviewId(
      Long reviewId, Long userId) {

    Assert.notNull(reviewId, REVIEW_ID_MUST_NOT_BE_NULL);

    ReviewWithLikeAndCommentCount data =
        selectReviewWithLikeAndCommentCount(userId)
            .where(review.isDeleted.isFalse(),
                review.id.eq(reviewId),
                filterIsNotPublic(userId))
            .groupBy(review.id)
            .fetchOne();

    return Optional.ofNullable(data);
  }

  @Override
  public Page<ReviewWithLikeAndCommentCount> findReviews(
      Long exhibitionId, Long userId, Pageable pageable) {

    Assert.notNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);

    List<ReviewWithLikeAndCommentCount> content =
        selectReviewWithLikeAndCommentCount(userId)
            .where(review.isDeleted.isFalse(),
                review.isPublic.isTrue(),
                reviewExhibitionIdEq(exhibitionId))
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
            reviewExhibitionIdEq(exhibitionId));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ReviewWithLikeAndCommentCount> findMyLikesReviews(
      Long currentUserId, Long targetUserId, Pageable pageable) {

    Assert.notNull(targetUserId, USER_ID_MUST_NOT_BE_NULL);
    Assert.notNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);

    List<ReviewWithLikeAndCommentCount> content =
        selectReviewWithLikeAndCommentCount(currentUserId)
            .leftJoin(reviewLikeToFilterTargetUser)
            .on(review.id.eq(reviewLikeToFilterTargetUser.review.id))
            .where(review.isDeleted.isFalse(),
                review.isPublic.isTrue(),
                reviewLikeTargetUserIdEq(targetUserId))
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
            reviewLikeTargetUserIdEq(targetUserId));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ReviewWithLikeAndCommentCount> findMyReviews(
      Long currentUserId, Long targetUserId, Pageable pageable) {

    Assert.notNull(targetUserId, USER_ID_MUST_NOT_BE_NULL);
    Assert.notNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);

    List<ReviewWithLikeAndCommentCount> content =
        selectReviewWithLikeAndCommentCount(currentUserId)
        .where(review.isDeleted.isFalse(),
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
            reviewTargetUserIdEq(targetUserId),
            filterIsNotPublic(currentUserId));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private JPAQuery<ReviewWithLikeAndCommentCount> selectReviewWithLikeAndCommentCount(Long userId) {
    return queryFactory.select(
            Projections.fields(ReviewWithLikeAndCommentCount.class,
                review.id.as("reviewId"),
                review.date,
                review.title,
                review.content,
                review.createdAt,
                review.updatedAt,
                new CaseBuilder()
                    .when(nullSafeConditions(reviewLikeUserIdEq(userId)))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                review.isPublic,
                reviewLike.id.countDistinct().as("likeCount"),
                comment.id.countDistinct().as("commentCount")
            ))
        .from(review)
        .leftJoin(reviewLikeToGetIsLiked).on(reviewLikeToGetIsLiked.review.eq(review),
            nullSafeConditions(reviewLikeUserIdEq(userId)))
        .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id))
        .leftJoin(comment).on(review.id.eq(comment.review.id), comment.isDeleted.isFalse());
  }

  private BooleanBuilder reviewTargetUserIdEq(Long targetUserId) {
    return nullSafeBooleanBuilder(() -> review.user.id.eq(targetUserId));
  }

  private BooleanBuilder reviewLikeTargetUserIdEq(Long targetUserId) {
    return nullSafeBooleanBuilder(() -> reviewLikeToFilterTargetUser.user.id.eq(targetUserId));
  }

  private BooleanBuilder reviewExhibitionIdEq(Long exhibitionId) {
    return nullSafeBooleanBuilder(() -> review.exhibition.id.eq(exhibitionId));
  }

  private BooleanBuilder reviewLikeUserIdEq(Long userId) {
    return nullSafeBooleanBuilder(() -> reviewLikeToGetIsLiked.user.id.eq(userId));
  }

  private BooleanExpression filterIsNotPublic(Long userId) {
    if (Objects.isNull(userId)) {
      return review.isPublic.isTrue();
    } else {
      return review.user.id.eq(userId).or(review.user.id.ne(userId).and(review.isPublic.isTrue()));
    }
  }

  private List<OrderSpecifier> getAllOrderSpecifiers(Pageable pageable) {
    if (pageable.getSort().isEmpty()) {
      return Collections.emptyList();
    }

    return pageable.getSort().stream()
        .map(order -> ReviewSortType.getReviewSortType(order.getProperty())
            .getOrderSpecifier(order.getDirection().isAscending() ? Order.ASC : Order.DESC))
        .collect(Collectors.toList());
  }

}
