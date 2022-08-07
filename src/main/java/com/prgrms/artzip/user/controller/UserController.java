package com.prgrms.artzip.user.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.jwt.JwtAuthenticationToken;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.common.jwt.claims.AccessClaim;
import com.prgrms.artzip.common.jwt.claims.Claims;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.exibition.service.ExhibitionLikeService;
import com.prgrms.artzip.exibition.service.ExhibitionService;
import com.prgrms.artzip.review.service.ReviewLikeService;
import com.prgrms.artzip.review.service.ReviewService;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.TokenReissueRequest;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import com.prgrms.artzip.user.dto.response.LoginResponse;
import com.prgrms.artzip.user.dto.response.SignUpResponse;
import com.prgrms.artzip.user.dto.response.TokenResponse;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.service.UserService;
import com.prgrms.artzip.user.service.UserUtilService;
import io.swagger.annotations.Api;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

import static com.prgrms.artzip.common.ErrorCode.*;
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

  private static SimpleGrantedAuthority apply(Role role) {
    return new SimpleGrantedAuthority(role.getAuthority().name());
  }


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

  @GetMapping("/reissue")
  public ResponseEntity<ApiResponse<LoginResponse>> reIssueAccessToken(@RequestBody @Valid
  TokenReissueRequest request) {
    User user = userUtilService.getUserById(request.getUserId());
    try {
      AccessClaim claims = jwtService.verifyAccessToken(request.getAccessToken());
      if (claims.getExp().getTime() - new Date().getTime() <= 1000 * 24 * 60 * 60) {
        throw new InvalidRequestException(
            TOKEN_NOT_EXPIRED);
      } else {
        throw new TokenExpiredException(TOKEN_EXPIRED.getMessage());
      }
    } catch (TokenExpiredException e) {
      jwtService.checkRefreshToken(user.getEmail(), request.getRefreshToken());
      List<GrantedAuthority> authorities = user.getRoles().stream().map(Role::toGrantedAuthority).collect(Collectors.toList());
      String newAccessToken = jwtService.createAccessToken(user.getId(), user.getEmail(), authorities);
      ApiResponse apiResponse = ApiResponse.builder()
          .message("토큰이 재발급되었습니다.")
          .status(OK.value())
          .data(new TokenResponse(newAccessToken))
          .build();
      return ResponseEntity.ok(apiResponse);
    }
  }
}
