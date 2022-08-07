package com.prgrms.artzip.user.controller;

import static com.prgrms.artzip.common.ErrorCode.MISSING_REQUEST_PARAMETER;
import static com.prgrms.artzip.common.ErrorCode.TOKEN_EXPIRED;
import static com.prgrms.artzip.common.ErrorCode.TOKEN_NOT_EXPIRED;
import static com.prgrms.artzip.common.ErrorCode.TOKEN_USER_ID_NOT_MATCHED;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.prgrms.artzip.comment.service.CommentService;
import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.jwt.JwtAuthenticationToken;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.common.jwt.claims.AccessClaim;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.exibition.service.ExhibitionLikeService;
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
import com.prgrms.artzip.user.dto.response.UniqueCheckResponse;
import com.prgrms.artzip.user.dto.response.UserResponse;
import com.prgrms.artzip.user.service.UserService;
import com.prgrms.artzip.user.service.UserUtilService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
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

  @ApiOperation(value = "중복 검사", notes = "이메일 및 닉네임에 대해 중복 검사를 진행합니다.")
  @GetMapping("/check")
  public ResponseEntity<ApiResponse<UniqueCheckResponse>> checkNicknameValid(
      @RequestParam(value = "nickname", required = false) String nickname,
      @RequestParam(value = "email", required = false) String email) {
    List<String> params = new ArrayList<>();
    params.add(nickname);
    params.add(email);
    boolean isUnique = makeUnanimousVote(params, List.of(userUtilService::checkNicknameUnique, userUtilService::checkEmailUnique));
    UniqueCheckResponse response = new UniqueCheckResponse(isUnique);
    ApiResponse apiResponse = ApiResponse.builder()
        .message("중복 검사가 완료되었습니다.")
        .status(OK.value())
        .data(response)
        .build();
    return ResponseEntity.ok(apiResponse);
  }

  private boolean makeUnanimousVote(List<String> params, List<Function<String, Boolean>> functions) {
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
    if (allParamsNull) throw new InvalidRequestException(MISSING_REQUEST_PARAMETER);
    return voteFlag;
  }

  @GetMapping("/token/reissue")
  public ResponseEntity<ApiResponse<LoginResponse>> reIssueAccessToken(@RequestBody @Valid
  TokenReissueRequest request) {
    User user = userUtilService.getUserById(request.getUserId());
    Date now = new Date();
    try {
      AccessClaim claims = jwtService.verifyAccessToken(request.getAccessToken());
      if (!claims.getUserId().equals(request.getUserId())) throw new InvalidRequestException(TOKEN_USER_ID_NOT_MATCHED);
      if (claims.getExp().getTime() - now.getTime() >= 1000 * 60 * 5) {
        throw new InvalidRequestException(TOKEN_NOT_EXPIRED);
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
