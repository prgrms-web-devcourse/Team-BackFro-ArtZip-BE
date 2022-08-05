package com.prgrms.artzip.exibition.domain.repository;

import static com.prgrms.artzip.exibition.domain.QExhibition.exhibition;
import static com.prgrms.artzip.exibition.domain.QExhibitionLike.exhibitionLike;
import static com.prgrms.artzip.review.domain.QReview.review;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import com.prgrms.artzip.exibition.domain.QExhibitionLike;
import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Month;
import com.prgrms.artzip.exibition.dto.ExhibitionCustomCondition;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionBasicForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionCustomRepository {

  private final JPAQueryFactory queryFactory;

  private final QExhibitionLike exhibitionLikeForIsLiked = new QExhibitionLike(
      "exhibitionLikeForIsLiked");

  @Override
  public Page<ExhibitionForSimpleQuery> findUpcomingExhibitions(Long userId, Pageable pageable) {
    LocalDate today = LocalDate.now();

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                new CaseBuilder()
                    .when(exhibitionLikeUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLike.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition), exhibitionLikeUserIdEq(userId))
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition))
        .where(exhibition.period.startDate.goe(today))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(exhibition.id)
        .orderBy(exhibition.period.startDate.asc(), exhibition.period.endDate.asc())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .where(exhibition.period.startDate.goe(today));

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ExhibitionForSimpleQuery> findMostLikeExhibitions(Long userId, boolean includeEnd,
      Pageable pageable) {
    BooleanBuilder mostLikeCondition = getMostLikeCondition(includeEnd);
    NumberPath<Long> likeCount = Expressions.numberPath(Long.class, "likeCount");

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                new CaseBuilder()
                    .when(exhibitionLikeUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLike.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition), exhibitionLikeUserIdEq(userId))
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition))
        .where(mostLikeCondition)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(exhibition.id)
        .orderBy(likeCount.desc())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .where(mostLikeCondition);

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
                    .when(exhibitionLikeUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibitionLike.exhibition.id.count().as("likeCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition), exhibitionLikeUserIdEq(userId))
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .where(exhibition.id.eq(exhibitionId))
        .groupBy(exhibition.id)
        .fetchOne());
  }

  @Override
  public Page<ExhibitionForSimpleQuery> findExhibitionsByQuery(Long userId, String query,
      boolean includeEnd,
      Pageable pageable) {
    BooleanBuilder exhibitionsByQueryCondition = getExhibitionsByQueryCondition(query, includeEnd);

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                new CaseBuilder()
                    .when(exhibitionLikeUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLike.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition), exhibitionLikeUserIdEq(userId))
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition))
        .where(exhibitionsByQueryCondition)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(exhibition.id)
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .where(exhibitionsByQueryCondition);

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
                    .when(exhibitionLikeUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLike.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition), exhibitionLikeUserIdEq(userId))
        .join(exhibition.exhibitionLikes, exhibitionLikeForExhibitionLikeUser)
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition))
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

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                new CaseBuilder()
                    .when(exhibitionLikeUserIdEq(userId))
                    .then(true)
                    .otherwise(false).as("isLiked"),
                exhibition.period,
                exhibitionLike.id.countDistinct().as("likeCount"),
                review.id.countDistinct().as("reviewCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLikeForIsLiked)
        .on(exhibitionLikeForIsLiked.exhibition.eq(exhibition), exhibitionLikeUserIdEq(userId))
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .leftJoin(review)
        .on(review.exhibition.eq(exhibition))
        .where(customCondition)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .groupBy(exhibition.id)
        .orderBy(exhibition.period.startDate.asc())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .where(customCondition);

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
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

  private BooleanExpression exhibitionLikeUserIdEq(Long userId) {
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
