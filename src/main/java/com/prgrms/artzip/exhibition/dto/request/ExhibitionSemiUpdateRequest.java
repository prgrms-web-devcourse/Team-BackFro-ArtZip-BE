package com.prgrms.artzip.exhibition.dto.request;

import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import lombok.Builder;
import lombok.Getter;

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
