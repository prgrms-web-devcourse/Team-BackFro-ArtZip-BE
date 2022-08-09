package com.prgrms.artzip.review.domain.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.util.function.Supplier;

public class QuerydslUtils {

  public static BooleanExpression alwaysTrue() {
    return Expressions.asBoolean(true).isTrue();
  }

  public static BooleanExpression alwaysFalse() {
    return Expressions.asBoolean(true).isFalse();
  }

  public static BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> f) {
    try {
      return new BooleanBuilder(f.get());
    } catch (IllegalArgumentException e) {
      return new BooleanBuilder();
    }
  }

}
