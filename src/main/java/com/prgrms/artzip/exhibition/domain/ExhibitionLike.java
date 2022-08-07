package com.prgrms.artzip.exhibition.domain;

import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_LIKE;
import static java.util.Objects.isNull;
import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "exhibition_like",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unq_exhibition_like_exhibition_id_user_id",
            columnNames = {"exhibition_id", "user_id"}
        )
    }
)
@NoArgsConstructor(access = PROTECTED)
public class ExhibitionLike extends BaseEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "exhibitionlike_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id")
  private Exhibition exhibition;


  public ExhibitionLike(User user, Exhibition exhibition) {
    validateExhibitionLikeField(user, exhibition);
    setUser(user);
    setExhibition(exhibition);
  }

  private void setUser(User user) {
    this.user = user;
  }

  private void setExhibition(Exhibition exhibition) {
    this.exhibition = exhibition;
    exhibition.getExhibitionLikes().add(this);
  }

  private void validateExhibitionLikeField(User user, Exhibition exhibition) {
    if (isNull(exhibition) || isNull(user)) {
      throw new InvalidRequestException(INVALID_EXHB_LIKE);
    }
  }
}
