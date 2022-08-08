package com.prgrms.artzip.review.domain.repository;

import static com.prgrms.artzip.review.domain.QReview.review;
import static com.prgrms.artzip.review.domain.QReviewLike.reviewLike;
import static com.prgrms.artzip.review.domain.repository.QuerydslUtils.alwaysFalse;
import static com.prgrms.artzip.review.domain.repository.QuerydslUtils.nullSafeBooleanBuilder;

import com.prgrms.artzip.review.domain.QReviewLike;
import com.prgrms.artzip.review.dto.projection.ReviewWithLikeData;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.EntityManager;

public class ReviewCustomRepositoryImpl implements ReviewCustomRepository {

  private final JPAQueryFactory queryFactory;

  private final QReviewLike RL = new QReviewLike("RL");

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
            reviewLike.user.id.count().as("likeCount")
        ))
        .from(review)
        .leftJoin(RL).on(RL.review.eq(review),
            alwaysFalse().or(reviewLikeUserIdEq(userId)))
        .leftJoin(reviewLike).on(review.id.eq(reviewLike.review.id))
        .where(review.isDeleted.eq(false),
            review.id.eq(reviewId),
            filterIsNotPublic(userId))
        .groupBy(review.id)
        .fetchOne();

    return Optional.ofNullable(data);
  }

  private BooleanBuilder reviewLikeUserIdEq(Long userId) {
    return nullSafeBooleanBuilder(() -> RL.user.id.eq(userId));
  }

  private BooleanExpression filterIsNotPublic(Long userId) {
    if (Objects.isNull(userId)) {
      return review.isPublic.eq(true);
    } else {
      return review.user.id.eq(userId).or(review.user.id.ne(userId).and(review.isPublic.eq(true)));
    }
  }

}
