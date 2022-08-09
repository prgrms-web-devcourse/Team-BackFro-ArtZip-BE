package com.prgrms.artzip.comment.domain;

import static lombok.AccessLevel.PROTECTED;

import java.io.Serializable;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class CommentLikeId implements Serializable {
  private Long commentId;
  private Long userId;
}
