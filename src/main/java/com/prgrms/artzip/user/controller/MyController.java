package com.prgrms.artzip.user.controller;

import static org.springframework.http.HttpStatus.*;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.review.dto.request.ReviewCreateRequest;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.dto.request.UserUpdateRequest;
import com.prgrms.artzip.user.dto.response.UserUpdateResponse;
import com.prgrms.artzip.user.service.UserService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"users-me"})
@RestController
@RequestMapping("api/v1/users/me")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class MyController {

  private final UserService userService;

  @PatchMapping("/info")
  public ResponseEntity<ApiResponse<UserUpdateResponse>> updateMyInfo(
      @CurrentUser User user,
      @RequestPart(value = "data") UserUpdateRequest request,
      @RequestPart(required = false) MultipartFile profileImage) {
    UserUpdateResponse updateResponse = userService.updateUserInfo(user, request, profileImage);
    ApiResponse apiResponse = ApiResponse.builder()
        .message("유저의 정보가 수정되었습니다.")
        .status(OK.value())
        .data(updateResponse)
        .build();
    return ResponseEntity.ok(apiResponse);
  }
}
