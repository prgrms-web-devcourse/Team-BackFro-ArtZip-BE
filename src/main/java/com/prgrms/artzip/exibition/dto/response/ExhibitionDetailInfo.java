package com.prgrms.artzip.exibition.dto.response;

import static java.util.Objects.isNull;

import com.prgrms.artzip.exibition.domain.Area;
import com.prgrms.artzip.exibition.domain.Genre;
import java.time.LocalDate;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ExhibitionDetailInfo extends ExhibitionBasicInfo{
  private LocalDate startDate;
  private LocalDate endDate;
  private String area; // enum 조심
  private String url;
  private String placeUrl;
  private String inquiry;
  private String genre; // enum 조심
  private String description;
  private long likeCount;
  private String placeAddress;
  private double lat;
  private double lng;
  private boolean isLiked;
  // reviews 추가 필요

  public ExhibitionDetailInfo(Long exhibitionId, String name, String thumbnail,
      LocalDate startDate, LocalDate endDate, Area area, String url, String placeUrl,
      String inquiry, Genre genre, String description, long likeCount, String placeAddress,
      double lat, double lng, boolean isLiked) {
    super(exhibitionId, name, thumbnail);
    this.startDate = startDate;
    this.endDate = endDate;
    this.area = isNull(area) ? "" : area.toString();
    this.url = url;
    this.placeUrl = placeUrl;
    this.inquiry = inquiry;
    this.genre = isNull(genre) ? "" : genre.toString();
    this.description = description;
    this.likeCount = likeCount;
    this.placeAddress = placeAddress;
    this.lat = lat;
    this.lng = lng;
    this.isLiked = isLiked;
  }
}
