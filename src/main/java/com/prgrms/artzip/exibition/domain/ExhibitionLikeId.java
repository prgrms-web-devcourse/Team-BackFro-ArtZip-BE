package com.prgrms.artzip.exibition.domain;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ExhibitionLikeId implements Serializable {
  private Long exhibitionId;
  private Long userId;

  public ExhibitionLikeId(Long exhibitionId, Long userId) {
    this.exhibitionId = exhibitionId;
    this.userId = userId;
  }
}
