package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Table(name = "review_photo")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReviewPhoto extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_photo_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id", referencedColumnName = "review_id")
  private Review review;

  @Column(name = "path", nullable = false, length = 2083)
  private String path;

  public ReviewPhoto(Review review, String path) {
    validateFields(review, path);
    this.review = review;
    this.path = path;
  }

  private void validateFields(Review review, String path) {
    validateNotNull(review, path);
    validatePath(path);
  }

  private void validateNotNull(Review review, String path) {
    Assert.notNull(review, "review는 필수값입니다.");
    Assert.notNull(path, "path는 필수값입니다.");
  }

  private void validatePath(String path) {
    Assert.isTrue(path.length() > 0 && path.length() <= 2083,
        "path는 1글자 이상 2083자 이하이어야 합니다.");
  }
}
