package com.prgrms.artzip.exhibition.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class ExhibitionsAroundMeResponse {
    private List<ExhibitionAroundMeInfoResponse> exhibitions;

    public ExhibitionsAroundMeResponse(List<ExhibitionAroundMeInfoResponse> exhibitions) {
        this.exhibitions = exhibitions;
    }
}
