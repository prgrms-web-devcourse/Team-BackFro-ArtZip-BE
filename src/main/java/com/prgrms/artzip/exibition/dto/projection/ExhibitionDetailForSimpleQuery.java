package com.prgrms.artzip.exibition.dto.projection;

import com.prgrms.artzip.exibition.domain.Location;
import com.prgrms.artzip.exibition.domain.Period;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExhibitionDetailForSimpleQuery {

  private Long id;
  private Integer seq;
  private String name;
  private Period period;
  private Genre genre;
  private String description;
  private Location location;
  private String inquiry;
  private String fee;
  private String thumbnail;
  private String url;
  private String placeUrl;
  private long likeCount;

  @Builder
  public ExhibitionDetailForSimpleQuery(Long id, Integer seq, String name,
      Period period, Genre genre, String description,
      Location location, String inquiry, String fee, String thumbnail, String url,
      String placeUrl, long likeCount) {
    this.id = id;
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
    this.likeCount = likeCount;
  }
}
