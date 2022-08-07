package com.prgrms.artzip.comment.domain;

import static com.prgrms.artzip.common.ErrorCode.COMMENT_NOT_FOUND;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.user.domain.User;
import java.util.Objects;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinColumnOrFormula;

@Getter
@Entity
@Table(name = "comment_like")
@NoArgsConstructor(access = PROTECTED)
public class CommentLike {

  @EmbeddedId
  private CommentLikeId commentLikeId;

  @MapsId("commentId")
  @ManyToOne(fetch = LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "comment_id", referencedColumnName = "comment_Id")
  )
  private Comment comment;

  @MapsId("userId")
  @ManyToOne(fetch = LAZY)
  @JoinColumnOrFormula(column =
  @JoinColumn(name = "user_id", referencedColumnName = "user_Id")
  )
  private User user;

  @Builder
  public CommentLike(Comment comment, User user) {
    setComment(comment);
    setUser(user);
    this.commentLikeId = new CommentLikeId(comment.getId(), user.getId());
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
