package com.prgrms.artzip.exhibition.domain.vo;

import com.prgrms.artzip.exhibition.domain.enumType.Area;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Enumerated;

import static javax.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Embeddable
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Location {

    @Column(name = "latitude", nullable = false)
    private Double latitude; // 위도, y

    @Column(name = "longitude", nullable = false)
    private Double longitude; // 경도, x

    @Enumerated(STRING)
    @Column(name = "area", nullable = false, length = 20)
    private Area area; // 지역

    @Column(name = "place", nullable = false, length = 50)
    private String place; // 장소

    @Column(name = "address", nullable = false, length = 100)
    private String address; // 장소의 주소

    @Builder
    public Location(Double latitude, Double longitude, Area area, String place, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.area = area;
        this.place = place;
        this.address = address;
    }
}
