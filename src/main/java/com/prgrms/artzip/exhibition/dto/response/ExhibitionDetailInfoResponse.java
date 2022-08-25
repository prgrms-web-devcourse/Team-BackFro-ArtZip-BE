package com.prgrms.artzip.exhibition.dto.response;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import com.prgrms.artzip.exhibition.domain.enumType.Genre;
import com.prgrms.artzip.exhibition.dto.projection.ExhibitionDetailForSimpleQuery;
import com.prgrms.artzip.review.dto.response.ReviewsResponseForExhibitionDetail;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ExhibitionDetailInfoResponse extends ExhibitionBasicInfoResponse {
    private LocalDate startDate;
    private LocalDate endDate;
    private Area area;
    private String url;
    private String placeUrl;
    private String inquiry;
    private Genre genre;
    private String description;
    private long likeCount;
    private long reviewCount;
    private String placeAddress;
    private double lat;
    private double lng;
    private Boolean isLiked;
    private List<ReviewsResponseForExhibitionDetail> reviews;

    public ExhibitionDetailInfoResponse(ExhibitionDetailForSimpleQuery exhibitionDetailForSimpleQuery, List<ReviewsResponseForExhibitionDetail> reviews) {
        super(exhibitionDetailForSimpleQuery.getId(), exhibitionDetailForSimpleQuery.getName(), exhibitionDetailForSimpleQuery.getThumbnail());
        this.startDate = exhibitionDetailForSimpleQuery.getPeriod().getStartDate();
        this.endDate = exhibitionDetailForSimpleQuery.getPeriod().getEndDate();
        this.area = exhibitionDetailForSimpleQuery.getLocation().getArea();
        this.url = exhibitionDetailForSimpleQuery.getUrl();
        this.placeUrl = exhibitionDetailForSimpleQuery.getPlaceUrl();
        this.inquiry = exhibitionDetailForSimpleQuery.getInquiry();
        this.genre = exhibitionDetailForSimpleQuery.getGenre();
        this.description = exhibitionDetailForSimpleQuery.getDescription();
        this.likeCount = exhibitionDetailForSimpleQuery.getLikeCount();
        this.reviewCount = exhibitionDetailForSimpleQuery.getReviewCount();
        this.placeAddress = exhibitionDetailForSimpleQuery.getLocation().getAddress();
        this.lat = exhibitionDetailForSimpleQuery.getLocation().getLatitude();
        this.lng = exhibitionDetailForSimpleQuery.getLocation().getLongitude();
        this.isLiked = exhibitionDetailForSimpleQuery.getIsLiked();
        this.reviews = reviews;
    }
}
