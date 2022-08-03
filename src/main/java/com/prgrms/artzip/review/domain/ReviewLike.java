package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.user.domain.User;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

@Entity
@Table(name = "review_like")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewLike extends BaseEntity {

  @EmbeddedId
  private ReviewLikeId reviewLikeId;

  @MapsId("reviewId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "review_id",
      referencedColumnName = "review_id")
  )
  private Review review;

  @MapsId("userId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "user_id",
      referencedColumnName = "user_id")
  )
  private User user;

  public ReviewLike(Review review, User user) {
    validateFields(review, user);
    this.reviewLikeId = new ReviewLikeId(review.getId(), user.getId());
    this.review = review;
    this.user = user;
  }

  private void validateFields(Review review, User user) {
    validateReview(review);
    validateUser(user);
  }

  private void validateReview(Review review) {
    if (Objects.isNull(review)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_LIKE_FIELD_CONTAINS_NULL_VALUE);
    }
  }

  private void validateUser(User user) {
    if (Objects.isNull(user)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_LIKE_FIELD_CONTAINS_NULL_VALUE);
    }
  }

}
