package com.prgrms.artzip.review.domain;

import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.exibition.domain.Exhibition;
import com.prgrms.artzip.user.domain.User;
import java.time.LocalDate;
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
import org.springframework.util.Assert;

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

  @Builder
  public Review(User user, Exhibition exhibition, String content, String title,
      LocalDate date, Boolean isPublic) {
    validateFields(user, exhibition, content, title, date, isPublic);
    this.user = user;
    this.exhibition = exhibition;
    this.content = content;
    this.title = title;
    this.date = date;
    this.isPublic = isPublic;
  }

  private void validateFields(User user, Exhibition exhibition, String content, String title,
      LocalDate date, Boolean isPublic) {
    validateNotNull(user, exhibition, content, title, date, isPublic);
    validateContent(content);
    validateTitle(title);
    validateDate(date);
  }

  private void validateNotNull(User user, Exhibition exhibition, String content, String title,
      LocalDate date, Boolean isPublic) {
    Assert.notNull(user, "리뷰 작성자는 필수값입니다.");
    Assert.notNull(exhibition, "전시회는 필수값입니다.");
    Assert.notNull(content, "리뷰 내용은 필수값입니다.");
    Assert.notNull(title, "리뷰 제목은 필수값입니다.");
    Assert.notNull(date, "방문일은 필수값입니다.");
    Assert.notNull(isPublic, "공개 여부는 필수값입니다.");
  }

  private void validateContent(String content) {
    Assert.isTrue(content.length() > 0 && content.length() <= 1000,
        "리뷰 내용은 1글자 이상 1000자 이하이어야 합니다.");
  }

  private void validateTitle(String title) {
    Assert.isTrue(title.length() > 0 && title.length() <= 50,
        "리뷰 제목은 1글자 이상 50자 이하이어야 합니다.");
  }

  private void validateDate(LocalDate date) {
    Assert.isTrue(date.compareTo(LocalDate.now()) <= 0,
        "방문일은 오늘 이후일 수 없습니다.");
  }
}
