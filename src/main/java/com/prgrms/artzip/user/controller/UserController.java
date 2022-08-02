package com.prgrms.artzip.user.controller;

import com.prgrms.artzip.common.ApiResponse;
import com.prgrms.artzip.common.jwt.JwtAuthenticationToken;
import com.prgrms.artzip.common.jwt.JwtPrincipal;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserRegisterRequest;
import com.prgrms.artzip.user.dto.response.LoginResponse;
import com.prgrms.artzip.user.dto.response.RegisterResponse;
import com.prgrms.artzip.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.http.HttpStatus.*;

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
                    .userId(principal.userId)
                    .accessToken(principal.accessToken)
                    .refreshToken(refreshToken)
                    .build())
            .build();
    return ResponseEntity.ok(response);
  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<RegisterResponse>> register(@RequestBody @Valid
      UserRegisterRequest request) {
    User newUser = userService.register(request);
    ApiResponse response = ApiResponse.builder()
            .message("회원가입 성공하였습니다.")
            .status(CREATED.value())
            .data(RegisterResponse.from(newUser))
            .build();
    return ResponseEntity.created(URI.create("/register")).body(response);
  }
}
