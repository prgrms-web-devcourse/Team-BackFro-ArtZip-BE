package com.prgrms.artzip.exhibition.dto.request;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
public class ExhibitionSemiUpdateRequest {
  private final String description;
  private final Genre genre;

  @Builder
  public ExhibitionSemiUpdateRequest(String description, Genre genre) {
    this.description = description;
    this.genre = genre;
  }
}
