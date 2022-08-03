package com.prgrms.artzip.user.dto.response;

import lombok.Builder;
import lombok.Getter;
@Getter
@Builder
public class UserResponse {
    private final Long userId;
    private final String profileImage;
    private final String nickname;
    private final Long reviewCount;
    private final Long likeCount;
    private final Long commentCount;
}
