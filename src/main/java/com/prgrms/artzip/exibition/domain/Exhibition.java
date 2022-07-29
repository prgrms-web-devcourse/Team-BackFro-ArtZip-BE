package com.prgrms.artzip.exibition.domain;

import static com.prgrms.artzip.common.ErrorCode.*;
import static java.util.Objects.*;
import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.StringUtils.*;

import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.BaseEntity;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.review.domain.Review;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

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

  @Builder
  public Exhibition(Integer seq, String name, LocalDate startDate, LocalDate endDate, Genre genre, String description, Double latitude, Double longitude, Area area, String place, String address, String inquiry, String fee, String thumbnail, String url, String placeUrl) {
    validateExhibitionField(seq, name, startDate, endDate, genre, description, latitude, longitude, area, place, address, inquiry, fee, thumbnail, url, placeUrl);
    this.seq = seq;
    this.name = name;
    this.period = new Period(startDate, endDate);
    this.genre = genre;
    this.description = description;
    this.location = new Location(latitude, longitude, area, place, address);
    this.inquiry = inquiry;
    this.fee = fee;
    this.thumbnail = thumbnail;
    this.url = url;
    this.placeUrl = placeUrl;
  }

  private void validateExhibitionField(Integer seq, String name, LocalDate startDate, LocalDate endDate, Genre genre, String description, Double latitude, Double longitude, Area area, String place, String address, String inquiry, String fee, String thumbnail, String url, String placeUrl) {
    validateSeq(seq);
    validateName(name);
    validatePeriod(startDate, endDate);
    validateDescription(description);
    validateLocation(latitude, longitude, area, place, address);
    validateInquiry(inquiry);
    validateFee(fee);
    validateThumbnail(thumbnail);
    validateUrl(url);
    validatePlaceUrl(placeUrl);
  }

  private void validateSeq(Integer seq) {
    if(isNull(seq)) {
      throw new InvalidRequestException(INVALID_EXHBN_SEQ);
    }
  }

  private void validateName(String name) {
    if(!hasText(name) || name.length() < 1 || name.length() > 70) {
      throw new InvalidRequestException(INVALID_EXHBN_NAME);
    }
  }

  private void validatePeriod(LocalDate startDate, LocalDate endDate) {
    if(isNull(startDate) || isNull(endDate) || startDate.isAfter(endDate)) {
      throw new InvalidRequestException(INVALID_EXHBN_PERIOD);
    }
  }

  private void validateDescription(String description) {
    if(hasText(description)) {
      if(description.length() < 1 || description.length() > 1000) {
        throw new InvalidRequestException(INVALID_EXHBN_DESCRIPTION);
      }
    }
  }

  private void validateLocation(Double latitude, Double longitude, Area area, String place, String address) {
    if(isNull(latitude) || isNull(longitude)) {
      throw new InvalidRequestException(INVALID_EXHBN_COORDINATE);
    }

    if(isNull(area)) {
      throw new InvalidRequestException(INVALID_EXHB_AREA);
    }

    if(isNull(place) || place.length() < 1 || place.length() > 20) {
      throw new InvalidRequestException(INVALID_EXHB_PLACE);
    }

    if(isNull(address) || address.length() < 1 || address.length() > 100) {
      throw new InvalidRequestException(INVALID_EXHB_ADDRESS);
    }
  }

  private void validateInquiry(String inquiry) {
    if(!hasText(inquiry) || inquiry.length() < 1 || inquiry.length() > 100) {
      throw new InvalidRequestException(INVALID_EXHB_INQUIRY);
    }
  }

  private void validateFee(String fee) {
    if(!hasText(fee) || fee.length() < 1 || fee.length() > 1000) {
      throw new InvalidRequestException(INVALID_EXHB_FEE);
    }
  }

  private void validateThumbnail(String thumbnail) {
    if(!hasText(thumbnail) || thumbnail.length() < 1 || thumbnail.length() > 2083 || !isValidUrl(thumbnail)) {
      throw new InvalidRequestException(INVALID_EXHB_THUMBNAIL);
    }
  }

  private void validateUrl(String url) {
    if(hasText(url)) {
      if(url.length() > 2083 || !isValidUrl(url)) {
        throw new InvalidRequestException(INVALID_EXHB_URL);
      }
    }
  }

  private void validatePlaceUrl(String placeUrl) {
    if(hasText(placeUrl)) {
      if(placeUrl.length() > 2083 || !isValidUrl(placeUrl)) {
        throw new InvalidRequestException(INVALID_EXHB_PLACEURL);
      }
    }
  }

  private boolean isValidUrl(String url) {
    Pattern pattern = Pattern.compile("[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)");
    Matcher matcher = pattern.matcher(url);
    return matcher.matches();
  }

}
