package com.prgrms.artzip.review.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewCreateRequest {

  @NotNull
  private Long exhibitionId;

  @NotNull
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotBlank
  private String title;

  @NotBlank
  private String content;

  @NotNull
  private Boolean isPublic;

  @Builder
  public ReviewCreateRequest(Long exhibitionId, LocalDate date, String title, String content,
      Boolean isPublic) {
    this.exhibitionId = exhibitionId;
    this.date = date;
    this.title = title;
    this.content = content;
    this.isPublic = isPublic;
  }
}
