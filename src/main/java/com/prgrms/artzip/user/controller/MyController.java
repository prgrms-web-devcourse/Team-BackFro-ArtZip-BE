package com.prgrms.artzip.user.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users/me")
public class MyController {
    private Logger log = LoggerFactory.getLogger(getClass());
    private final UserService userService;

    public MyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo (@CurrentUser User user) {
        log.info("user: {}", user.getId());
        return null;
    }
}
