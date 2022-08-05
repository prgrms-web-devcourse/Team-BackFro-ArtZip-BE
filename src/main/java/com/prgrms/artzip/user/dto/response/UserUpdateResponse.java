package com.prgrms.artzip.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateResponse {
    private final Long userId;
    private final String profileImage;
    private final String nickname;
    private final String email;
}
