package com.prgrms.artzip.exhibition.repository;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.exhibition.domain.QExhibition;
import com.prgrms.artzip.exhibition.domain.QExhibitionLike;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum ExhibitionSortType {
    CREATED_AT("createdAt", QExhibition.exhibition.createdAt),
    EXHIBITION_ID("id", QExhibition.exhibition.id),
    LIKE_COUNT("likeCount", new QExhibitionLike(
            "exhibitionLikeForLikeCount").id.countDistinct()),
    START_DATE("startDate", QExhibition.exhibition.period.startDate),
    END_DATE("endDate", QExhibition.exhibition.period.endDate);

    private final String property;
    private final Expression target;

    public OrderSpecifier<?> getOrderSpecifier(Order direction) {
        return new OrderSpecifier(direction, this.target);
    }

    public static ExhibitionSortType getExhibitionSortType(String property) {
        return Arrays.stream(ExhibitionSortType.values())
                .filter(exhibitionSortType -> exhibitionSortType.property.equals(property))
                .findAny().orElseThrow(() -> new NotFoundException(ErrorCode.INVALID_EXHB_SORT_TYPE));
    }
}
