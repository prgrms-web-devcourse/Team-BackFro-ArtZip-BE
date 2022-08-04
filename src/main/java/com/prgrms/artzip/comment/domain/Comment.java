package com.prgrms.artzip.comment.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.DuplicateRequestException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.review.domain.Review;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "comment")
public class Comment extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "comment_id")
  private Long id;

  @Column(name = "content", nullable = false, length = 500)
  private String content;

  //TODO migration 으로 해당 column db에 alter 시키기
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  //TODO migration 으로 해당 column db에 alter 시키기
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id")
  private Review review;

  @Builder
  public Comment(String content, User user, Comment parent, Review review) {
    setContent(content);
    setUser(user);
    this.parent = parent;
    this.review = review;
  }

  public void setContent(String content) {
    if (Objects.isNull(content) || content.isBlank()) {
      throw new InvalidRequestException(ErrorCode.CONTENT_IS_REQUIRED);
    }
    if (content.length() > 500) {
      throw new InvalidRequestException(ErrorCode.CONTENT_IS_TOO_LONG);
    }
    this.content = content;
  }

  private void setUser(User user) {
    if (Objects.isNull(user)) {
      throw new InvalidRequestException(ErrorCode.COMMENT_USER_IS_REQUIRED);
    }
    this.user = user;
  }

  public void softDelete() {
    if (this.isDeleted) {
      throw new DuplicateRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
    }
    this.isDeleted = true;
  }
}
