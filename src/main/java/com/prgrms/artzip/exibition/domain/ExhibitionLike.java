package com.prgrms.artzip.exibition.domain;

import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_LIKE;
import static java.util.Objects.isNull;
import static javax.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.user.domain.User;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = PROTECTED)
public class ExhibitionLike {
  @EmbeddedId
  private ExhibitionLikeId exhibitionLikeId;

  @MapsId("exhibitionId")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id")
  private Exhibition exhibition;

  @MapsId("userId")
  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  public ExhibitionLike(Exhibition exhibition, User user) {
    validateExhibitionLikeField(exhibition, user);
    this.exhibitionLikeId = new ExhibitionLikeId(exhibition.getId(), user.getId());
    setExhibition(exhibition);
    setUser(user);
  }

  private void setExhibition(Exhibition exhibition) {
    this.exhibition = exhibition;
    exhibition.getExhibitionLikes().add(this);
  }

  private void setUser(User user) {
    this.user = user;
  }

  private void validateExhibitionLikeField(Exhibition exhibition, User user) {
    if(isNull(exhibition) || isNull(user)) {
      throw new InvalidRequestException(INVALID_EXHB_LIKE);
    }
  }
}
