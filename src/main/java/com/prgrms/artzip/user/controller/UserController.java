package com.prgrms.artzip.user.controller;

import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.jwt.JwtAuthenticationToken;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.exhibition.service.ExhibitionLikeService;
import com.prgrms.artzip.review.service.ReviewLikeService;
import com.prgrms.artzip.review.service.ReviewService;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import com.prgrms.artzip.user.dto.response.LoginResponse;
import com.prgrms.artzip.user.dto.response.SignUpResponse;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.service.UserService;
import com.prgrms.artzip.user.service.UserUtilService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

@Api(tags = {"users"})
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserController {

  private final UserService userService;

  private final UserUtilService userUtilService;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  private final ReviewService reviewService;

  private final CommentService commentService;

  private final ExhibitionLikeService exhibitionLikeService;

  private final ReviewLikeService reviewLikeService;


  @PostMapping("/local/login")
  public ResponseEntity<ApiResponse<LoginResponse>> localLogin(
      @RequestBody @Valid UserLocalLoginRequest request) {
    JwtAuthenticationToken authToken = new JwtAuthenticationToken(request.getEmail(),
        request.getPassword());
    Authentication authentication = authenticationManager.authenticate(authToken);
    String refreshToken = (String) authentication.getDetails();
    JwtPrincipal principal = (JwtPrincipal) authentication.getPrincipal();
    ApiResponse response = ApiResponse.builder()
        .message("로그인 성공하였습니다.")
        .status(OK.value())
        .data(LoginResponse.builder()
            .userId(principal.getUser().getId())
            .accessToken(principal.getAccessToken())
            .refreshToken(refreshToken)
            .build())
        .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid
      UserSignUpRequest request) {
    User newUser = userService.signUp(request);
    ApiResponse response = ApiResponse.builder()
        .message("회원가입 성공하였습니다.")
        .status(CREATED.value())
        .data(SignUpResponse.from(newUser))
        .build();
    return ResponseEntity.created(URI.create("/signup")).body(response);
  }

  @GetMapping("/{userId}/info")
  public ResponseEntity<ApiResponse<UserRepository>> getUserInfo(
      @PathVariable("userId") Long userId) {
    User user = userUtilService.getUserById(userId);
    UserResponse userResponse = UserResponse.builder()
        .userId(user.getId())
        .nickname(user.getNickname())
        .profileImage(user.getProfileImage())
        .email(user.getEmail())
        .reviewCount(reviewService.getReviewCountByUserId(user.getId()))
        .exhibitionLikeCount(exhibitionLikeService.getExhibitionLikeCountByUserId(user.getId()))
        .reviewLikeCount(reviewLikeService.getReviewLikeCountByUserId(user.getId()))
        .commentCount(commentService.getCommentCountByUserId(user.getId())).build();
    ApiResponse apiResponse = ApiResponse.builder()
        .message("유저 정보 조회 성공하였습니다.")
        .status(OK.value())
        .data(userResponse)
        .build();
    return ResponseEntity.ok(apiResponse);
  }
}
