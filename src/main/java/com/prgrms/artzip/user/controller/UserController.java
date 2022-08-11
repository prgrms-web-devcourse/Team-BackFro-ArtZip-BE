package com.prgrms.artzip.user.controller;

import static com.prgrms.artzip.common.ErrorCode.MISSING_REQUEST_PARAMETER;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.PageResponse;
import com.prgrms.artzip.common.entity.CurrentUser;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.jwt.JwtAuthenticationToken;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.exhibition.domain.ExhibitionLike;
import com.prgrms.artzip.exhibition.dto.response.ExhibitionInfoResponse;
import com.prgrms.artzip.exhibition.service.ExhibitionLikeService;
import com.prgrms.artzip.exhibition.service.ExhibitionService;
import com.prgrms.artzip.review.dto.response.ReviewsResponse;
import com.prgrms.artzip.review.service.ReviewLikeService;
import com.prgrms.artzip.review.service.ReviewService;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.TokenReissueRequest;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import com.prgrms.artzip.user.dto.response.LoginResponse;
import com.prgrms.artzip.user.dto.response.SignUpResponse;
import com.prgrms.artzip.user.dto.response.TokenResponse;
import com.prgrms.artzip.user.dto.response.UniqueCheckResponse;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.service.UserService;
import com.prgrms.artzip.user.service.UserUtilService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"유저 API"})
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

  private final ExhibitionService exhibitionService;

  private final ExhibitionLikeService exhibitionLikeService;

  private final ReviewLikeService reviewLikeService;


  @ApiOperation(value = "로컬 로그인", notes = "이메일과 비밀번호로 로컬로그인을 진행합니다.")
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

  @ApiOperation(value = "회원가입", notes = "회원가입을 합니다.")
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

  @ApiOperation(value = "유저 정보 조회", notes = "유저 정보를 조회합니다.")
  @GetMapping("/{userId}/info")
  public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(
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

  @ApiOperation(value = "중복 검사", notes = "이메일 및 닉네임에 대해 중복 검사를 진행합니다.")
  @GetMapping("/check")
  public ResponseEntity<ApiResponse<UniqueCheckResponse>> checkNicknameValid(
      @RequestParam(value = "nickname", required = false) String nickname,
      @RequestParam(value = "email", required = false) String email) {
    List<String> params = new ArrayList<>();
    params.add(nickname);
    params.add(email);
    boolean isUnique = makeUnanimousVote(params,
        List.of(userUtilService::checkNicknameUnique, userUtilService::checkEmailUnique));
    UniqueCheckResponse response = new UniqueCheckResponse(isUnique);
    ApiResponse apiResponse = ApiResponse.builder()
        .message("중복 검사가 완료되었습니다.")
        .status(OK.value())
        .data(response)
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  private boolean makeUnanimousVote(List<String> params,
      List<Function<String, Boolean>> functions) {
    boolean allParamsNull = true;
    boolean voteFlag = true;
    int idx = 0;
    for (String param : params) {
      if (!isNull(param)) {
        voteFlag = voteFlag && functions.get(idx).apply(param);
        allParamsNull = false;
      }
      idx++;
    }
    if (allParamsNull) {
      throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    }
    return voteFlag;
  }

  @ApiOperation(value = "토큰 재발행", notes = "토큰을 재발행합니다.")
  @PostMapping("/token/reissue")
  public ResponseEntity<ApiResponse<LoginResponse>> reissueAccessToken(@RequestBody @Valid
  TokenReissueRequest request) {
    User user = userUtilService.getUserById(request.getUserId());
    String newAccessToken = jwtService.reissueAccessToken(user, request.getAccessToken(),
        request.getRefreshToken());
    ApiResponse apiResponse = ApiResponse.builder()
        .message("토큰이 재발급되었습니다.")
        .status(OK.value())
        .data(new TokenResponse(newAccessToken))
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  @ApiOperation(value = "로그아웃", notes = "요청한 accessToken을 로그아웃 토큰으로 저장합니다.")
  @PatchMapping("/logout")
  public ResponseEntity<ApiResponse<Object>> logout(
      @AuthenticationPrincipal JwtPrincipal principal) {
    jwtService.logout(principal.getAccessToken());
    ApiResponse apiResponse = ApiResponse.builder()
        .message("로그아웃 성공하였습니다.")
        .status(OK.value())
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  @ApiOperation(value = "유저가 좋아요 누른 전시회 조회", notes = "유저가 좋아요 누른 전시회를 조회합니다.")
  @GetMapping("/{userId}/info/exhibitions/like")
  public ResponseEntity<ApiResponse<PageResponse<ExhibitionInfoResponse>>> getUserLikeExhibitions(
      @CurrentUser User user,
      @PathVariable("userId") Long userId,
      @PageableDefault(page = 0, size = 4) Pageable pageable
  ) {
    ApiResponse apiResponse = ApiResponse.builder()
        .message("유저가 좋아요 누른 전시회를 조회하였습니다.")
        .status(OK.value())
        .data(new PageResponse(
            exhibitionService.getUserLikeExhibitions(
                isNull(user) ? null : user.getId(), userId,
                pageable)))
        .build();

    return ResponseEntity
        .ok()
        .body(apiResponse);
  }

  @GetMapping("{userId}/info/review/like")
  public ResponseEntity<ApiResponse<PageResponse<ReviewsResponse>>> getUserLikeReviews(
      @CurrentUser User user,
      @PathVariable("userId") Long userId,
      @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

    PageResponse<ReviewsResponse> response = reviewService.getReviewsForMyLikes(user, userId, pageable);

    return ResponseEntity.ok()
        .body(ApiResponse.<PageResponse<ReviewsResponse>>builder()
            .message("유저가 좋아요 누른 후기 리스트 조회 성공")
            .status(HttpStatus.OK.value())
            .data(response)
            .build());
  }
}
