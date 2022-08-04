package com.prgrms.artzip.user.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.jwt.JwtAuthenticationToken;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import com.prgrms.artzip.user.dto.response.LoginResponse;
import com.prgrms.artzip.user.dto.response.SignUpResponse;
import com.prgrms.artzip.user.service.UserService;
import io.swagger.annotations.Api;
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
@SuppressWarnings({"rawtypes", "unchecked"})
public class UserController {

  private final UserService userService;

  private final JwtService jwtService;

  private final AuthenticationManager authenticationManager;

  public UserController(UserService userService,
                        JwtService jwtService,
                        AuthenticationManager authenticationManager) {
    this.userService = userService;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/local/login")
  public ResponseEntity<ApiResponse<LoginResponse>> localLogin(@RequestBody @Valid UserLocalLoginRequest request) {
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

  @PostMapping("/sign-up")
  public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid
                                                            UserSignUpRequest request) {
    User newUser = userService.signUp(request);
    ApiResponse response = ApiResponse.builder()
            .message("회원가입 성공하였습니다.")
            .status(CREATED.value())
            .data(SignUpResponse.from(newUser))
            .build();
    return ResponseEntity.created(URI.create("/register")).body(response);
  }
}
