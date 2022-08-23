package com.prgrms.artzip.common.util;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import java.util.function.Supplier;

public class QueryDslCustomUtils {
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

  /**
   * If all conditions are null,
   * returns BooleanExpression that is always false
   * @param conditions
   * @return BooleanExpression
   */
  public static Predicate nullSafeConditions(Predicate... conditions) {
    BooleanBuilder booleanBuilder = new BooleanBuilder();
    for (Predicate condition : conditions) {
      booleanBuilder.and(condition);
    }
    return alwaysFalse().or(booleanBuilder);
  }
}
