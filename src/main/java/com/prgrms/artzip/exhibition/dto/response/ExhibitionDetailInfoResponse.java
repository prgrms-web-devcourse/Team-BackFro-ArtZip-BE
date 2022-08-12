package com.prgrms.artzip.exhibition.dto.response;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ExhibitionDetailInfoResponse extends ExhibitionBasicInfoResponse {

  private LocalDate startDate;
  private LocalDate endDate;
  private Area area;
  private String url;
  private String placeUrl;
  private String inquiry;
  private Genre genre;
  private String description;
  private long likeCount;
  private long reviewCount;
  private String placeAddress;
  private double lat;
  private double lng;
  private Boolean isLiked;
  private List<ReviewsResponseForExhibitionDetail> reviews;

  public ExhibitionDetailInfoResponse(Long exhibitionId, String name, String thumbnail,
      LocalDate startDate, LocalDate endDate, Area area, String url, String placeUrl,
      String inquiry, Genre genre, String description, long likeCount, long reviewCount,
      String placeAddress,
      double lat, double lng, boolean isLiked, List<ReviewsResponseForExhibitionDetail> reviews) {
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
    this.reviewCount = reviewCount;
    this.placeAddress = placeAddress;
    this.lat = lat;
    this.lng = lng;
    this.isLiked = isLiked;
    this.reviews = reviews;
  }
}
