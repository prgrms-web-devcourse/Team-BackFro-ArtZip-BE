package com.prgrms.artzip.exhibition.dto.response;

import com.prgrms.artzip.exhibition.dto.projection.ExhibitionForSimpleQuery;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionWithLocationForSimpleQuery;
import lombok.Getter;

@Getter
public class ExhibitionAroundMeInfoResponse extends ExhibitionInfoResponse {

    private String placeAddr;
    private Double lat;
    private Double lng;

    public ExhibitionAroundMeInfoResponse(ExhibitionWithLocationForSimpleQuery exhibitionWithLocationForSimpleQuery) {
        super(ExhibitionForSimpleQuery.builder()
                .id(exhibitionWithLocationForSimpleQuery.getId())
                .name(exhibitionWithLocationForSimpleQuery.getName())
                .thumbnail(exhibitionWithLocationForSimpleQuery.getThumbnail())
                .isLiked(exhibitionWithLocationForSimpleQuery.getIsLiked())
                .period(exhibitionWithLocationForSimpleQuery.getPeriod())
                .likeCount(exhibitionWithLocationForSimpleQuery.getLikeCount())
                .reviewCount(exhibitionWithLocationForSimpleQuery.getReviewCount())
                .build());

        this.placeAddr = exhibitionWithLocationForSimpleQuery.getLocation().getAddress();
        this.lat = exhibitionWithLocationForSimpleQuery.getLocation().getLatitude();
        this.lng = exhibitionWithLocationForSimpleQuery.getLocation().getLongitude();
    }
}
