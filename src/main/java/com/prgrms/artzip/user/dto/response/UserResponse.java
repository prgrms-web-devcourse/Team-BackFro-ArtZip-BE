package com.prgrms.artzip.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private final Long userId;
    private final String profileImage;
    private final String nickname;
    private final String email;
    private final Long reviewCount;
    private final Long reviewLikeCount;
    private final Long exhibitionLikeCount;
    private final Long commentCount;
}
