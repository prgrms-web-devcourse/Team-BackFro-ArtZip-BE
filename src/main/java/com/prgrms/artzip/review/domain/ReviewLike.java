package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.user.domain.User;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

@Entity
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
    this.reviewLikeId = new ReviewLikeId(review.getId(), user.getId());
    this.review = review;
    this.user = user;
  }
}
