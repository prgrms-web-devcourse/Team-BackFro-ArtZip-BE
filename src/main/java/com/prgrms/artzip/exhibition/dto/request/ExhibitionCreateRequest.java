package com.prgrms.artzip.exhibition.dto.request;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;

public record ExhibitionCreateRequest(@NotNull String name, String description,
                                      @NotNull String fee,
                                      Genre genre,
                                      String inquiry, @NotNull String address,
                                      @NotNull Area area,
                                      @NotNull Double latitude, @NotNull Double longitude,
                                      @NotNull String place, @NotNull LocalDate startDate,
                                      @NotNull LocalDate endDate, String placeUrl,
                                      @NotNull String thumbnail, String url) {

}
