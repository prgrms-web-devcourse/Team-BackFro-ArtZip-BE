package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.user.domain.User;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review_like",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "user_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewLike extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_like_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", referencedColumnName = "review_id")
  private Review review;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  public ReviewLike(Review review, User user) {
    validateFields(review, user);
    setReview(review);
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

  public void setReview(Review review) {
    validateReview(review);
    if (Objects.nonNull(this.review)) {
      this.review.getReviewLikes().remove(this);
    }

    this.review = review;
    review.getReviewLikes().add(this);
  }
}
