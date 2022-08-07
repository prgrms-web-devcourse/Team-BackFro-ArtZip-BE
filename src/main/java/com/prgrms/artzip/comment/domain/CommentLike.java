package com.prgrms.artzip.comment.domain;

import static com.prgrms.artzip.common.ErrorCode.COMMENT_NOT_FOUND;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.User;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comment_like",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unq_comment_id_user_id",
            columnNames = {"comment_id", "user_id"}
        )
    })
@NoArgsConstructor(access = PROTECTED)
public class CommentLike {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "comment_like_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  public CommentLike(Comment comment, User user) {
    setComment(comment);
    setUser(user);
  }

  private void setComment(Comment comment) throws NotFoundException {
    if (Objects.isNull(comment)) {
      throw new NotFoundException(COMMENT_NOT_FOUND);
    }
    this.comment = comment;
  }

  private void setUser(User user) throws NotFoundException {
    if (Objects.isNull(user)) {
      throw new NotFoundException(COMMENT_NOT_FOUND);
    }
    this.user = user;
  }
}
