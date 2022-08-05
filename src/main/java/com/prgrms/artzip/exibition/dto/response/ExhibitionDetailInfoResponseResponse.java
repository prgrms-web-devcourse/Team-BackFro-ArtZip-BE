package com.prgrms.artzip.exibition.dto.response;

import com.prgrms.artzip.exibition.domain.enumType.Area;
import com.prgrms.artzip.exibition.domain.enumType.Genre;
import java.time.LocalDate;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ExhibitionDetailInfoResponseResponse extends ExhibitionBasicInfoResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private Area area;
  private String url;
  private String placeUrl;
  private String inquiry;
  private Genre genre;
  private String description;
  private long likeCount;
  private String placeAddress;
  private double lat;
  private double lng;
  private Boolean isLiked;
  // reviews

  public ExhibitionDetailInfoResponseResponse(Long exhibitionId, String name, String thumbnail,
      LocalDate startDate, LocalDate endDate, Area area, String url, String placeUrl,
      String inquiry, Genre genre, String description, long likeCount, String placeAddress,
      double lat, double lng, boolean isLiked) {
    super(exhibitionId, name, thumbnail);
    this.startDate = startDate;
    this.endDate = endDate;
    this.area = area;
    this.url = url;
    this.placeUrl = placeUrl;
    this.inquiry = inquiry;
    this.genre = genre;
    this.description = description;
    this.likeCount = likeCount;
    this.placeAddress = placeAddress;
    this.lat = lat;
    this.lng = lng;
    this.isLiked = isLiked;
  }
}
