package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
import java.util.Date;
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

}
