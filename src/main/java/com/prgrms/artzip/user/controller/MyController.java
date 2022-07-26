package com.prgrms.artzip.user.controller;

import static org.springframework.http.HttpStatus.OK;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.PasswordUpdateRequest;
import com.prgrms.artzip.user.dto.request.UserUpdateRequest;
import com.prgrms.artzip.user.dto.response.SimpleUserResponse;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.dto.response.UserUpdateResponse;
import com.prgrms.artzip.user.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"로그인한 유저 전용 API"})
@RestController
@RequestMapping("api/v1/users/me")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class MyController {

  private final UserService userService;

  @ApiOperation(value = "내 정보 수정", notes = "로그인한 유저의 정보를 수정합니다.")
  @PatchMapping("/info")
  public ResponseEntity<ApiResponse<UserUpdateResponse>> updateMyInfo(
      @CurrentUser User user,
      @Parameter(name = "data", schema = @Schema(type = "string", format = "binary"))
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

  @ApiOperation(value = "비밀번호 변경", notes = "비밀번호를 변경합니다.")
  @PatchMapping("/password")
  public ResponseEntity<ApiResponse> updatePassword(@CurrentUser LocalUser user, @RequestBody @Valid
      PasswordUpdateRequest request) {
    userService.updatePassword(user, request);
    ApiResponse apiResponse = ApiResponse.builder()
        .message("유저의 비밀번호가 변경되었습니다.")
        .status(OK.value())
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  @ApiOperation(value = "내 정보 조회", notes = "로그인한 유저의 정보를 조회합니다.")
  @GetMapping("/info")
  public ResponseEntity<ApiResponse<Object>> getUserInfo(
      @CurrentUser User user) {

    ApiResponse apiResponse = ApiResponse.builder()
        .message("내 정보를 조회하였습니다.")
        .status(OK.value())
        .data(SimpleUserResponse.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .profileImage(user.getProfileImage())
            .build())
        .build();
    return ResponseEntity.ok(apiResponse);
  }
}
