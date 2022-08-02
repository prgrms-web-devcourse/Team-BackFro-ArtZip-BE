package com.prgrms.artzip.comment.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.review.domain.Review;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
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

  @GeneratedValue
  @Id
  @Column(name = "comment_id")
  private Long id;

  @Column(name = "content", nullable = false, length = 500)
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Comment parent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id")
  private Review review;

  @Builder
  public Comment(String content, Comment parent, Review review) {
    setContent(content);
    this.parent = parent;
    this.review = review;
  }

  private void setContent(String content) {
    if (content == null || content.isBlank()) {
      throw new InvalidRequestException(ErrorCode.CONTENT_IS_REQUIRED);
    }
    if (content.length() > 500) {
      throw new InvalidRequestException(ErrorCode.CONTENT_IS_TOO_LONG);
    }
    this.content = content;
  }
}
