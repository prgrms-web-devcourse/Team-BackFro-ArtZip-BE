package com.prgrms.artzip.exibition.domain;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import com.prgrms.artzip.common.entity.BaseEntity;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "exhibition")
@NoArgsConstructor(access = PROTECTED)
@Getter
public class Exhibition extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "exhibition_id")
  private Long id;

  @Column(name = "seq", unique = true)
  private String seq;

  @Column(name = "name", nullable = false, length = 70)
  private String name;

  @Embedded
  private Period period;

  @Enumerated(STRING)
  @Column(name = "genre", length = 20)
  private Genre genre;

  @Column(name = "description", length = 1000)
  private String description;

  @Embedded
  private Location location;

  @Column(name = "inquiry", length = 100)
  private String inquiry;

  @Column(name = "fee", length = 100)
  private String fee;

  @Column(name = "thumbnail", length = 2083)
  private String thumbnail;

  @Column(name = "url", length = 2083)
  private String url;

  @Column(name = "placeUrl", length = 2083)
  private String placeUrl;

  @Builder
  public Exhibition(String seq, String name, Period period,
      Genre genre, String description, Location location, String inquiry, String fee,
      String thumbnail, String url, String placeUrl) {
    this.seq = seq;
    this.name = name;
    this.period = period;
    this.genre = genre;
    this.description = description;
    this.location = location;
    this.inquiry = inquiry;
    this.fee = fee;
    this.thumbnail = thumbnail;
    this.url = url;
    this.placeUrl = placeUrl;
  }
}
