package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    if (Objects.isNull(review)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_PHOTO_FIELD_CONTAINS_NULL_VALUE);
    } else if (Objects.isNull(path)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_PHOTO_FIELD_CONTAINS_NULL_VALUE);
    }
  }

  private void validatePath(String path) {
    if (path.isBlank() || path.length() > 2083) {
      throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_PHOTO_PATH_LENGTH);
    }
  }
}
