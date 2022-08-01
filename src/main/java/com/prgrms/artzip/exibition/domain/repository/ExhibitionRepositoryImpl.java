package com.prgrms.artzip.exibition.domain.repository;

import static com.prgrms.artzip.exibition.domain.QExhibition.exhibition;
import static com.prgrms.artzip.exibition.domain.QExhibitionLike.exhibitionLike;
import static com.prgrms.artzip.review.domain.QReview.review;

import com.prgrms.artzip.exibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.exibition.dto.projection.ExhibitionForSimpleQuery;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionCustomRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ExhibitionForSimpleQuery> findUpcomingExhibitions(Pageable pageable) {
    LocalDate today = LocalDate.now();

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                exhibition.period,
                exhibitionLike.exhibition.id.count().as("likeCount"),
                review.exhibition.id.count().as("reviewCount")
            )
        )
        .from(exhibition)
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
  public Page<ExhibitionForSimpleQuery> findMostLikeExhibitions(boolean includeEnd,
      Pageable pageable) {
    BooleanBuilder mostLikeCondition = getMostLikeCondition(includeEnd);
    NumberPath<Long> likeCount = Expressions.numberPath(Long.class, "likeCount");

    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
                exhibition.id,
                exhibition.name,
                exhibition.thumbnail,
                exhibition.period,
                exhibitionLike.exhibition.id.count().as("likeCount"),
                review.exhibition.id.count().as("reviewCount")
            )
        )
        .from(exhibition)
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
  public Optional<ExhibitionDetailForSimpleQuery> findExhibition(Long exhibitionId) {
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
                exhibitionLike.exhibition.id.count().as("likeCount")
            )
        )
        .from(exhibition)
        .leftJoin(exhibitionLike)
        .on(exhibitionLike.exhibition.eq(exhibition))
        .where(exhibition.id.eq(exhibitionId))
        .groupBy(exhibition.id)
        .fetchOne());
  }


  private BooleanBuilder getMostLikeCondition(boolean includeEnd) {
    BooleanBuilder mostLikeCondition = new BooleanBuilder();

    if (!includeEnd) {
      mostLikeCondition.and(exhibition.period.endDate.goe(LocalDate.now()));
    }

    return mostLikeCondition;
  }
}
