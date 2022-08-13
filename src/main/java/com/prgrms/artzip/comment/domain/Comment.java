package com.prgrms.artzip.comment.domain;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.DuplicateRequestException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.review.domain.Review;
import com.prgrms.artzip.user.domain.User;
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
import org.hibernate.annotations.Formula;

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

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id", referencedColumnName = "comment_id")
  private Comment parent;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id")
  private Review review;

  @OneToMany(mappedBy = "comment")
  private List<CommentLike> commentLikes = new ArrayList<>();

  @Formula("(select count(*) from comment_like where comment_like.comment_id=comment_id)")
  private Long likeCount;

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

  public void softDelete() {
    if (this.isDeleted) {
      throw new DuplicateRequestException(ErrorCode.COMMENT_ALREADY_DELETED);
    }
    this.isDeleted = true;
  }

  public void addLike(CommentLike commentLike) {
    this.commentLikes.add(commentLike);
  }

  private void setUser(User user) {
    if (Objects.isNull(user)) {
      throw new InvalidRequestException(ErrorCode.COMMENT_USER_IS_REQUIRED);
    }
    this.user = user;
  }
}
