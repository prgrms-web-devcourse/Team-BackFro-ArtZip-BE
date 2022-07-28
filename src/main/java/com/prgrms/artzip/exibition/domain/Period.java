package com.prgrms.datahandle.exhibition.domain;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Period {
  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;
  @Column(name = "end_date", nullable = false)
  private LocalDate endDate;

  public Period(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }
}
