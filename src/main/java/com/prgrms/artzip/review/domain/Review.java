package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Review extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "review_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id")
  private Exhibition exhibition;

  @Column(name = "content", nullable = false, length = 1000)
  private String content;

  @Column(name = "title", nullable = false, length = 50)
  private String title;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "is_public", nullable = false)
  private Boolean isPublic;

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @OneToMany(mappedBy = "review")
  private List<ReviewLike> reviewLikes = new ArrayList<>();

  @OneToMany(mappedBy = "review")
  private List<ReviewPhoto> reviewPhotos = new ArrayList<>();

  @Builder
  public Review(User user, Exhibition exhibition, String content, String title,
      LocalDate date, Boolean isPublic) {
    validateFields(user, exhibition, content, title, date, isPublic);
    this.user = user;
    this.exhibition = exhibition;
    this.content = content;
    this.title = title;
    this.date = date;
    this.isPublic = isPublic;
    this.isDeleted = false;
  }

  private void validateFields(User user, Exhibition exhibition, String content, String title,
      LocalDate date, Boolean isPublic) {
    validateUser(user);
    validateExhibition(exhibition);
    validateContent(content);
    validateTitle(title);
    validateDate(date);
    validateIsPublic(isPublic);
  }

  private void validateUser(User user) {
    if (Objects.isNull(user)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
  }

  private void validateExhibition(Exhibition exhibition) {
    if (Objects.isNull(exhibition)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
  }

  private void validateContent(String content) {
    if (Objects.isNull(content)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
    if (content.isBlank() || content.length() > 1000) {
      throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_CONTENT_LENGTH);
    }
  }

  private void validateTitle(String title) {
    if (Objects.isNull(title)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
    if (title.isBlank() || title.length() > 50) {
      throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_TITLE_LENGTH);
    }
  }

  private void validateDate(LocalDate date) {
    if (Objects.isNull(date)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
    if (date.compareTo(LocalDate.now()) > 0) {
      throw new InvalidRequestException(ErrorCode.INVALID_REVIEW_DATE);
    }
  }

  private void validateIsPublic(Boolean isPublic) {
    if (Objects.isNull(isPublic)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
  }

  private void validateIsDeleted(Boolean isDeleted) {
    if (Objects.isNull(isDeleted)) {
      throw new InvalidRequestException(ErrorCode.REVIEW_FIELD_CONTAINS_NULL_VALUE);
    }
  }

  public void updateContent(String content) {
    validateContent(content);
    this.content = content;
  }

  public void updateTitle(String title) {
    validateTitle(title);
    this.title = title;
  }

  public void updateDate(LocalDate date) {
    validateDate(date);
    this.date = date;
  }

  public void updateIsPublic(Boolean isPublic) {
    validateIsPublic(isPublic);
    this.isPublic = isPublic;
  }

  public void updateIdDeleted(Boolean isDeleted) {
    validateIsDeleted(isDeleted);
    this.isDeleted = isDeleted;
  }
}
