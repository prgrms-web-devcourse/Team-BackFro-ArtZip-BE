package com.prgrms.artzip.review.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReviewUpdateRequest {

  @NotNull
  @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotBlank
  private String title;

  @NotBlank
  private String content;

  @NotNull
  private Boolean isPublic;

  @NotNull
  private List<Long> deletedPhotos;

  @Builder
  public ReviewUpdateRequest(LocalDate date, String title, String content, Boolean isPublic,
      List<Long> deletedPhotos) {
    this.date = date;
    this.title = title;
    this.content = content;
    this.isPublic = isPublic;
    this.deletedPhotos = deletedPhotos;
  }

}
