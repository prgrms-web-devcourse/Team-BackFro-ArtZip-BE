package com.prgrms.artzip.exhibition.domain;

import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHBN_COORDINATE;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHBN_DESCRIPTION;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHBN_NAME;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHBN_PERIOD;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_ADDRESS;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_AREA;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_FEE;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_INQUIRY;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_PLACE;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_PLACEURL;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_THUMBNAIL;
import static com.prgrms.artzip.common.ErrorCode.INVALID_EXHB_URL;
import static java.util.Objects.isNull;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.StringUtils.hasText;

import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.vo.Location;
import com.prgrms.artzip.exhibition.domain.vo.Period;
import com.prgrms.artzip.review.domain.Review;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "exhibition_id")
  private Long id;

  @Column(name = "seq", unique = true)
  private Integer seq;

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

  @Column(name = "fee", nullable = false, length = 1000)
  private String fee;

  @Column(name = "thumbnail", nullable = false, length = 2083)
  private String thumbnail;

  @Column(name = "url", length = 2083)
  private String url;

  @Column(name = "placeUrl", length = 2083)
  private String placeUrl;

  @OneToMany(mappedBy = "exhibition")
  private List<ExhibitionLike> exhibitionLikes = new ArrayList<>();

  @OneToMany(mappedBy = "exhibition")
  private List<Review> reviews = new ArrayList<>();

  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted;

  @Builder
  public Exhibition(Integer seq, String name, LocalDate startDate, LocalDate endDate, Genre genre,
      String description, Double latitude, Double longitude, Area area,
      String place, String address, String inquiry, String fee, String thumbnail, String url,
      String placeUrl) {
    this.seq = seq;
    setName(name);
    setPeriod(startDate, endDate);
    this.genre = genre;
    setDescription(description);
    setLocation(latitude, longitude, area, place, address);
    setInquiry(inquiry);
    setFee(fee);
    setThumbnail(thumbnail);
    setUrl(url);
    setPlaceUrl(placeUrl);
    this.isDeleted = false;
  }

  public void deleteExhibition() {
    this.isDeleted = true;
  }

  private void setName(String name) {
    if (!hasText(name) || name.length() > 70) {
      throw new InvalidRequestException(INVALID_EXHBN_NAME);
    }
    this.name = name;
  }

  private void setPeriod(LocalDate startDate, LocalDate endDate) {
    if (isNull(startDate) || isNull(endDate) || startDate.isAfter(endDate)) {
      throw new InvalidRequestException(INVALID_EXHBN_PERIOD);
    } else {
      this.period = new Period(startDate, endDate);
    }
  }

  private void setDescription(String description) {
    if (hasText(description)) {
      if (description.length() > 1000) {
        throw new InvalidRequestException(INVALID_EXHBN_DESCRIPTION);
      }
      this.description = description;
    }
  }

  private void setLocation(Double latitude, Double longitude, Area area, String place,
      String address) {
    if (isNull(latitude) || isNull(longitude) || latitude < -90 || latitude > 90 || longitude < -180
        || longitude > 180) {
      throw new InvalidRequestException(INVALID_EXHBN_COORDINATE);
    } else if (isNull(area)) {
      throw new InvalidRequestException(INVALID_EXHB_AREA);
    } else if (isNull(place) || place.length() > 20) {
      throw new InvalidRequestException(INVALID_EXHB_PLACE);
    } else if (isNull(address) || address.length() > 100) {
      throw new InvalidRequestException(INVALID_EXHB_ADDRESS);
    } else {
      this.location = new Location(latitude, longitude, area, place, address);
    }
  }

  private void setInquiry(String inquiry) {
    if (!hasText(inquiry) || inquiry.length() > 100) {
      throw new InvalidRequestException(INVALID_EXHB_INQUIRY);
    }
    this.inquiry = inquiry;
  }

  private void setFee(String fee) {
    if (!hasText(fee) || fee.length() > 1000) {
      throw new InvalidRequestException(INVALID_EXHB_FEE);
    }
    this.fee = fee;
  }

  private void setThumbnail(String thumbnail) {
    if (!hasText(thumbnail) || thumbnail.length() > 2083 || !isValidUrl(thumbnail)) {
      throw new InvalidRequestException(INVALID_EXHB_THUMBNAIL);
    }
    this.thumbnail = thumbnail;
  }

  private void setUrl(String url) {
    if (hasText(url)) {
      if (url.length() > 2083 || !isValidUrl(url)) {
        throw new InvalidRequestException(INVALID_EXHB_URL);
      }
      this.url = url;
    }
  }

  private void setPlaceUrl(String placeUrl) {
    if (hasText(placeUrl)) {
      if (placeUrl.length() > 2083 || !isValidUrl(placeUrl)) {
        throw new InvalidRequestException(INVALID_EXHB_PLACEURL);
      }
      this.placeUrl = placeUrl;
    }
  }

  private boolean isValidUrl(String url) {
    Pattern pattern = Pattern.compile(
        "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    Matcher matcher = pattern.matcher(url);
    return matcher.matches();
  }

}
