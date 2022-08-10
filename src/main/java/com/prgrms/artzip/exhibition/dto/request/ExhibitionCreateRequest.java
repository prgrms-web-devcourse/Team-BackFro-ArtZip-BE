package com.prgrms.artzip.exhibition.dto.request;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ExhibitionCreateRequest {
  @NotNull
  private final String name;
  private final String description;
  @NotNull
  private final String fee;
  private final Genre genre;
  private final String inquiry;
  @NotNull
  private final String address;
  @NotNull
  private final Area area;
  @NotNull
  private final Double latitude;
  @NotNull
  private final Double longitude;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private final LocalDate startDate;
  @NotNull
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private final LocalDate endDate;
  private final String placeUrl;
  private final String url;
//  @NotNull
//  private final MultipartFile thumbnail;
  private final String place;

  @Builder
  public ExhibitionCreateRequest(String name, String description,
      String fee, Genre genre, String inquiry, String address,
      Area area, Double latitude, Double longitude, LocalDate startDate, LocalDate endDate,
      String placeUrl, String url, String place) {
    this.name = name;
    this.description = description;
    this.fee = fee;
    this.genre = genre;
    this.inquiry = inquiry;
    this.address = address;
    this.area = area;
    this.latitude = latitude;
    this.longitude = longitude;
    this.startDate = startDate;
    this.endDate = endDate;
    this.placeUrl = placeUrl;
    this.url = url;
//    this.thumbnail = thumbnail;
    this.place = place;
  }
}
