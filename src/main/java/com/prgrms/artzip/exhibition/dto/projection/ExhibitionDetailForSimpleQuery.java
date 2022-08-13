package com.prgrms.artzip.exhibition.dto.projection;

import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.domain.vo.Location;
import com.prgrms.artzip.exhibition.domain.vo.Period;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ExhibitionDetailForSimpleQuery extends ExhibitionBasicForSimpleQuery {

  private Integer seq;
  private Period period;
  private Genre genre;
  private String description;
  private Location location;
  private String inquiry;
  private String fee;
  private String url;
  private String placeUrl;
  private Boolean isLiked;
  private long likeCount;
  private long reviewCount;
}
