package com.prgrms.artzip.exhibition.domain.repository;

import static com.prgrms.artzip.exhibition.domain.QExhibition.exhibition;
import static com.prgrms.artzip.review.domain.QReview.review;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.prgrms.artzip.exhibition.domain.QExhibitionLike;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Month;
import com.prgrms.artzip.exhibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class ExhibitionRepositoryImpl implements ExhibitionCustomRepository {

  private final JPAQueryFactory queryFactory;

  private final QExhibitionLike exhibitionLikeForIsLiked = new QExhibitionLike(
      "exhibitionLikeForIsLiked");
  private final QExhibitionLike exhibitionLikeForLikeCount = new QExhibitionLike(
      "exhibitionLikeForLikeCount");

  @Override
  public Page<ExhibitionForSimpleQuery> findUpcomingExhibitions(Long userId, Pageable pageable) {
    BooleanBuilder upcomingCondition = getUpcomingCondition();

    List<ExhibitionForSimpleQuery> exhibitions = findExhibitions(userId, upcomingCondition,
        Arrays.asList(
            new OrderSpecifier(Order.ASC, exhibition.period.startDate),
            new OrderSpecifier(Order.ASC, exhibition.period.endDate)),
        pageable);

    JPAQuery<Long> countQuery = getExhibitionCountQuery(upcomingCondition);

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ExhibitionForSimpleQuery> findMostLikeExhibitions(Long userId, boolean includeEnd,
      Pageable pageable) {
    BooleanBuilder mostLikeCondition = getMostLikeCondition(includeEnd);

    List<ExhibitionForSimpleQuery> exhibitions = findExhibitions(userId,
        mostLikeCondition,
        List.of(new OrderSpecifier(Order.DESC, Expressions.numberPath(Long.class, "likeCount"))),
        pageable);

    JPAQuery<Long> countQuery = getExhibitionCountQuery(mostLikeCondition);

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }

  @Override
  public Optional<ExhibitionDetailForSimpleQuery> findExhibition(Long userId, Long exhibitionId) {
    return Optional.ofNullable(queryFactory
        .select(Projections.fields(ExhibitionDetailForSimpleQuery.class,
                exhibition.id,
                exhibition.seq,
                exhibition.name,
                exhibition.period,
                exhibition.genre,
                exhibition.description,
                exhibition.location,
                exhibition.inquiry,
                exhibition.fee,
                exhibition.thumbnail,
                exhibition.url,
                exhibition.placeUrl,
                new CaseBuilder()
                    .when(exhibitionLikeForIsLikedUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibitionLikeForLikeCount.id.count().as("likeCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition),
            exhibitionLikeForIsLikedUserIdEq(userId))
        .leftJoin(exhibitionLikeForLikeCount)
        .on(exhibitionLikeForLikeCount.exhibition.eq(exhibition))
        .where(exhibition.id.eq(exhibitionId))
        .groupBy(exhibition.id)
        .fetchOne());
  }

  @Override
  public Page<ExhibitionForSimpleQuery> findExhibitionsByQuery(Long userId, String query,
      boolean includeEnd, Pageable pageable) {
    BooleanBuilder exhibitionsByQueryCondition = getExhibitionsByQueryCondition(query, includeEnd);

    List<ExhibitionForSimpleQuery> exhibitions = findExhibitions(userId,
        exhibitionsByQueryCondition,
        List.of(new OrderSpecifier(Order.ASC, exhibition.id)),
        pageable);

    JPAQuery<Long> countQuery = getExhibitionCountQuery(exhibitionsByQueryCondition);

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }

  @Override
  public List<ExhibitionBasicForSimpleQuery> findExhibitionsForReview(String query) {
    BooleanBuilder exhibitionsForReviewCondition = getExhibitionsForReviewCondition(query);

    return queryFactory
        .select(Projections.fields(ExhibitionBasicForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail
            )
        )
        .from(exhibition)
        .where(exhibitionsForReviewCondition)
        .limit(30)
        .fetch();
  }

  @Override
  public Page<ExhibitionForSimpleQuery> findUserLikeExhibitions(Long userId,
      Long exhibitionLikeUserId, Pageable pageable) {
    // userId : 로그인 유저
    // exhibitionLikeUserId : 조회 대상
    QExhibitionLike exhibitionLikeForExhibitionLikeUser = new QExhibitionLike(
        "exhibitionLikeForExhibitionLikeUser");

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                new CaseBuilder()
                    .when(exhibitionLikeForIsLikedUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLikeForLikeCount.id.countDistinct().as("likeCount"),
                exhibitionLikeForLikeCount.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition),
            exhibitionLikeForIsLikedUserIdEq(userId))
        .join(exhibition.exhibitionLikes, exhibitionLikeForExhibitionLikeUser)
        .leftJoin(exhibitionLikeForLikeCount)
        .on(exhibitionLikeForLikeCount.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition), review.isDeleted.isFalse())
        .where(exhibitionLikeForExhibitionLikeUser.user.id.eq(exhibitionLikeUserId))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(exhibition.id, exhibitionLikeForExhibitionLikeUser.createdAt)
        .orderBy(exhibitionLikeForExhibitionLikeUser.createdAt.desc())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .join(exhibition.exhibitionLikes, exhibitionLikeForExhibitionLikeUser)
        .where(exhibitionLikeForExhibitionLikeUser.user.id.eq(exhibitionLikeUserId));

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ExhibitionForSimpleQuery> findExhibitionsByCustomCondition(Long userId,
      ExhibitionCustomCondition exhibitionCustomCondition, Pageable pageable) {
    BooleanBuilder customCondition = getCustomCondition(exhibitionCustomCondition);

    List<ExhibitionForSimpleQuery> exhibitions = findExhibitions(userId, customCondition,
        List.of(new OrderSpecifier(Order.ASC, exhibition.period.startDate)),
        pageable);

    JPAQuery<Long> countQuery = getExhibitionCountQuery(customCondition);

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }

  private List<ExhibitionForSimpleQuery> findExhibitions(Long userId, BooleanBuilder condition,
      List<OrderSpecifier> orders, Pageable pageable) {
    return queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                new CaseBuilder()
                    .when(exhibitionLikeForIsLikedUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLikeForLikeCount.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition),
            exhibitionLikeForIsLikedUserIdEq(userId))
        .leftJoin(exhibitionLikeForLikeCount)
        .on(exhibitionLikeForLikeCount.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition), review.isDeleted.isFalse())
        .where(condition)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(exhibition.id)
        .orderBy(orders.toArray(OrderSpecifier[]::new))
        .fetch();
  }

  private JPAQuery<Long> getExhibitionCountQuery(BooleanBuilder condition) {
    return queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .where(condition);
  }

  private BooleanBuilder getUpcomingCondition() {
    BooleanBuilder upcomingCondition = new BooleanBuilder();
    LocalDate today = LocalDate.now();

    upcomingCondition.and(exhibition.period.startDate.goe(today));

    return upcomingCondition;
  }

  private BooleanBuilder getMostLikeCondition(boolean includeEnd) {
    BooleanBuilder mostLikeCondition = new BooleanBuilder();

    mostLikeCondition
        .and(!includeEnd ? exhibitionEndDateGoe() : null);

    return mostLikeCondition;
  }

  private BooleanBuilder getExhibitionsByQueryCondition(String query, boolean includeEnd) {
    BooleanBuilder exhibitionsByQueryCondition = new BooleanBuilder();

    exhibitionsByQueryCondition
        .and(exhibitionNameContains(query))
        .and(!includeEnd ? exhibitionEndDateGoe() : null);

    return exhibitionsByQueryCondition;
  }

  private BooleanBuilder getExhibitionsForReviewCondition(String query) {
    BooleanBuilder exhibitionsForReviewCondition = new BooleanBuilder();

    exhibitionsForReviewCondition
        .and(exhibitionNameContains(query));

    return exhibitionsForReviewCondition;
  }

  private BooleanBuilder getCustomCondition(ExhibitionCustomCondition exhibitionCustomCondition) {
    BooleanBuilder customCondition = new BooleanBuilder();

    Set<Area> areas = exhibitionCustomCondition.getAreas();
    if (nonNull(areas) && !areas.isEmpty() && !areas.contains(Area.ALL)) {
      BooleanBuilder areaCondition = new BooleanBuilder();
      areas.forEach(area -> areaCondition.or(exhibition.location.area.eq(area)));
      customCondition.and(areaCondition);
    }

    Set<Month> months = exhibitionCustomCondition.getMonths();
    if (nonNull(months) && !months.isEmpty() && !months.contains(Month.ALL)) {
      BooleanBuilder monthCondition = new BooleanBuilder();

      months.forEach(month -> {
        BooleanBuilder periodCondition = new BooleanBuilder();

        LocalDate monthStart = LocalDate.of(LocalDate.now().getYear(), month.ordinal(), 1);
        LocalDate monthEnd = monthStart.plusDays(monthStart.lengthOfMonth() - 1);

        periodCondition
            .and(exhibition.period.startDate.loe(monthEnd))
            .and(exhibition.period.endDate.goe(monthStart));

        monthCondition.or(periodCondition);
      });

      customCondition.and(monthCondition);
    }

    customCondition
        .and(!exhibitionCustomCondition.getIncludeEnd() ? exhibitionEndDateGoe() : null);

    return customCondition;
  }

  private BooleanExpression exhibitionLikeForIsLikedUserIdEq(Long userId) {
    if (isNull(userId)) {
      return exhibitionLikeForIsLiked.user.id.eq(-1L);
    } else {
      return exhibitionLikeForIsLiked.user.id.eq(userId);
    }
  }

  private BooleanExpression exhibitionEndDateGoe() {
    return exhibition.period.endDate.goe(LocalDate.now());
  }

  private BooleanExpression exhibitionNameContains(String name) {
    return name == null ? null : exhibition.name.contains(name);
  }
}