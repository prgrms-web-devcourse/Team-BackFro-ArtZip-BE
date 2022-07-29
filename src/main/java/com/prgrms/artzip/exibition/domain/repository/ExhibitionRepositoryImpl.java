package com.prgrms.artzip.exibition.domain.repository;

import static com.prgrms.artzip.exibition.domain.QExhibition.exhibition;
import static com.prgrms.artzip.exibition.domain.QExhibitionLike.exhibitionLike;
import static com.prgrms.artzip.review.domain.QReview.review;
import static com.querydsl.core.types.ExpressionUtils.count;

import com.prgrms.artzip.exibition.dto.ExhibitionForSimpleQuery;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class ExhibitionRepositoryImpl implements ExhibitionCustomRepository{
  private final JPAQueryFactory queryFactory;

  @Override
  public Page<ExhibitionForSimpleQuery> findUpcomingExhibition(LocalDate today, Pageable pageable) {
    List<ExhibitionForSimpleQuery> exhibitions = queryFactory
        .select(Projections.fields(ExhibitionForSimpleQuery.class,
            exhibition.id.as("exhibitionId"), exhibition.name, exhibition.thumbnail, exhibition.period,
            ExpressionUtils.as(
                JPAExpressions.select(count(exhibitionLike))
                    .from(exhibitionLike)
                    .where(exhibitionLike.exhibition.id.eq(exhibition.id)),
                "likeCount"),
            ExpressionUtils.as(
                JPAExpressions.select(count(review))
                    .from(review)
                    .where(review.exhibition.id.eq(exhibition.id)),
                "reviewCount")))
        .from(exhibition)
        .where(exhibition.period.startDate.goe(today))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .orderBy(exhibition.period.startDate.asc(), exhibition.period.endDate.asc())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory
        .select(exhibition.count())
        .from(exhibition)
        .where();

    return PageableExecutionUtils.getPage(exhibitions, pageable, countQuery::fetchOne);
  }
}
