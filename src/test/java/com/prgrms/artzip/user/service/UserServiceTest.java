package com.prgrms.artzip.user.service;

import static com.prgrms.artzip.common.ErrorCode.INVALID_INPUT_VALUE;
import static com.prgrms.artzip.common.ErrorCode.LOGIN_PARAM_REQUIRED;
import static com.prgrms.artzip.common.ErrorCode.NICKNAME_ALREADY_EXISTS;
import static com.prgrms.artzip.common.ErrorCode.ROLE_NOT_FOUND;
import static com.prgrms.artzip.common.ErrorCode.USER_ALREADY_EXISTS;
import static com.prgrms.artzip.common.ErrorCode.USER_NOT_FOUND;
import static com.prgrms.artzip.common.ErrorCode.USER_PROFILE_NOT_MATCHED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.prgrms.artzip.common.Authority;
import com.prgrms.artzip.common.error.exception.AlreadyExistsException;
import com.prgrms.artzip.common.error.exception.InvalidRequestException;
import com.prgrms.artzip.common.error.exception.NotFoundException;
import com.prgrms.artzip.common.util.AmazonS3Remover;
import com.prgrms.artzip.common.util.AmazonS3Uploader;
import com.prgrms.artzip.user.domain.LocalUser;
import com.prgrms.artzip.user.domain.Role;
import com.prgrms.artzip.user.domain.User;
import com.prgrms.artzip.user.domain.repository.RoleRepository;
import com.prgrms.artzip.user.domain.repository.UserRepository;
import com.prgrms.artzip.user.dto.request.PasswordUpdateRequest;
import com.prgrms.artzip.user.dto.request.UserLocalLoginRequest;
import com.prgrms.artzip.user.dto.request.UserSignUpRequest;
import com.prgrms.artzip.user.dto.request.UserUpdateRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @InjectMocks
  private UserService userService;

  @Spy
  private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Mock
  AmazonS3Uploader amazonS3Uploader;

  @Mock
  AmazonS3Remover amazonS3Remover;

  private static final Role userRole = new Role(Authority.USER);

  private final User testUser = LocalUser.builder()
      .email(testEmail)
      .nickname(testNickname)
      .password(passwordEncoder.encode(testPassword))
      .roles(List.of(userRole)).build();
  private final MultipartFile testProfileFile = new MockMultipartFile(
      "testImage",
      "testImage.png",
      MediaType.MULTIPART_FORM_DATA_VALUE,
      "testImage".getBytes());

  private static final String testPassword = "test1234!";
  private static final String testEmail = "test@gmail.com";

  private static final String testNickname = "testUser";

  private static final String testProfileLink = "testProfileLink";

  private static final String PROFILE_DIRECTORY_NAME = "profileImage";

  private static final String defaultImageLink = "default";

  @Test
  @DisplayName("정상 회원가입 테스트")
  void testSignUp() {
    UserSignUpRequest signUpRequest = UserSignUpRequest.builder()
        .email(testUser.getEmail())
        .nickname(testUser.getNickname())
        .password(testPassword).build();
    // given
    when(userRepository.existsByEmailAndIsQuit(signUpRequest.getEmail(), false)).thenReturn(false);
    when(userRepository.existsByNicknameAndIsQuit(signUpRequest.getNickname(), false)).thenReturn(
        false);
    when(roleRepository.findByAuthority(Authority.USER)).thenReturn(Optional.of(userRole));
    when(userRepository.save(any())).thenReturn(testUser);

    //when
    User userResult = userService.signUp(signUpRequest);

    //then
    assertThat(userResult.getEmail()).isEqualTo(testUser.getEmail());
    assertThat(userResult.getNickname()).isEqualTo(testUser.getNickname());
    assertThat(userResult.getRoles()).containsAll(testUser.getRoles());
    verify(roleRepository).findByAuthority(Authority.USER);
    verify(userRepository).save(any());
  }

  @Test
  @DisplayName("회원가입 시 이메일이 이미 존재하는 경우 테스트")
  void testEmailExistSignUp() {
    UserSignUpRequest signUpRequest = UserSignUpRequest.builder()
        .email(testUser.getEmail())
        .nickname(testUser.getNickname())
        .password(testPassword).build();
    // given
    when(userRepository.existsByEmailAndIsQuit(signUpRequest.getEmail(), false)).thenReturn(true);

    assertThatThrownBy(() -> userService.signUp(signUpRequest))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage(USER_ALREADY_EXISTS.getMessage());
  }

  @Test
  @DisplayName("회원가입 시 닉네임이 이미 존재하는 경우 테스트")
  void testNicknameExistSignUp() {
    UserSignUpRequest signUpRequest = UserSignUpRequest.builder()
        .email(testUser.getEmail())
        .nickname(testUser.getNickname())
        .password(testPassword).build();
    // given
    when(userRepository.existsByNicknameAndIsQuit(signUpRequest.getNickname(), false)).thenReturn(
        true);

    assertThatThrownBy(() -> userService.signUp(signUpRequest))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage(USER_ALREADY_EXISTS.getMessage());
  }

  @Test
  @DisplayName("정상 로그인 테스트")
  void testUserLogin() {
    UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(testUser.getEmail(),
        testPassword);

    // given
    when(userRepository.findByEmailAndIsQuit(localLoginRequest.getEmail(), false)).thenReturn(
        Optional.of(testUser));

    // when
    User userResult = userService.login(localLoginRequest.getEmail(),
        localLoginRequest.getPassword());

    // then
    assertThat(userResult.getEmail()).isEqualTo(testUser.getEmail());
    assertThat(userResult.getNickname()).isEqualTo(testUser.getNickname());
    assertThat(userResult.getRoles()).containsAll(testUser.getRoles());
    verify(userRepository).findByEmailAndIsQuit(localLoginRequest.getEmail(), false);
  }

  @Test
  @DisplayName("없는 유저에 대한 로그인 테스트")
  void testAnonymousLogin() {
    // given
    UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(testUser.getEmail(),
        testPassword);

    // when
    when(userRepository.findByEmailAndIsQuit(localLoginRequest.getEmail(), false)).thenReturn(
        Optional.empty());

    // then
    assertThatThrownBy(
        () -> userService.login(localLoginRequest.getEmail(), localLoginRequest.getPassword()))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(USER_NOT_FOUND.getMessage());
  }

  @ParameterizedTest
  @MethodSource("errorLoginParameter")
  void testLoginParameter(String email, String password) {
    //given
    UserLocalLoginRequest localLoginRequest = new UserLocalLoginRequest(email, password);

    //when then
    assertThatThrownBy(
        () -> userService.login(localLoginRequest.getEmail(), localLoginRequest.getPassword()))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage(LOGIN_PARAM_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("회원가입 시 user role 에러 테스트")
  void testAuthoritySignUp() {
    //given
    UserSignUpRequest signUpRequest = UserSignUpRequest.builder()
        .email(testUser.getEmail())
        .nickname(testUser.getNickname())
        .password(testPassword).build();
    when(roleRepository.findByAuthority(Authority.USER)).thenReturn(Optional.empty());
    //when then
    assertThatThrownBy(() -> userService.signUp(signUpRequest))
        .isInstanceOf(NotFoundException.class)
        .hasMessage(ROLE_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("유저 정보 수정 : 닉네임 중복")
  void testNicknameNotUniqueWhenUpdateUserInfo() {
    // given
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage(testProfileLink)
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(true);

    // when then
    assertThatThrownBy(() -> userService.updateUserInfo(testUser, request, testProfileFile))
        .isInstanceOf(AlreadyExistsException.class)
        .hasMessage(NICKNAME_ALREADY_EXISTS.getMessage());
  }

  @Test
  @DisplayName("유저 정보 수정 : 기본이미지 -> A이미지(파일O)")
  void testUpdateNewImage() throws IOException {
    // given
    testUser.setProfileImage(defaultImageLink);
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage(testProfileLink)
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(false);
    when(amazonS3Uploader.getDefaultProfileImage()).thenReturn(defaultImageLink);
    when(amazonS3Uploader.upload(eq(testProfileFile), eq(PROFILE_DIRECTORY_NAME))).thenReturn(
        "new link");
    // when
    userService.updateUserInfo(testUser, request, testProfileFile);

    // then
    verify(amazonS3Uploader).upload(eq(testProfileFile),
        eq(PROFILE_DIRECTORY_NAME));
    verify(userRepository).save(testUser);
    assertThat(testUser).hasFieldOrPropertyWithValue("profileImage", "new link");
  }

  @ParameterizedTest
  @DisplayName("유저 정보 수정 : 기본이미지 -> 기본이미지(파일X)")
  @MethodSource("emptyFileParameterWithDefault")
  void testUpdateDefaultToDefault(MultipartFile file, String emptyProfileLink) {
    // given
    testUser.setProfileImage(defaultImageLink);
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage(emptyProfileLink)
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(false);
    when(amazonS3Uploader.getDefaultProfileImage()).thenReturn(defaultImageLink);

    // when
    userService.updateUserInfo(testUser, request, file);

    // then
    verify(userRepository).save(testUser);
    assertThat(testUser).hasFieldOrPropertyWithValue("profileImage", defaultImageLink);
  }

  @ParameterizedTest
  @DisplayName("유저 정보 수정 : A이미지 -> 기본이미지(파일X)")
  @MethodSource("emptyFileParameterWithDefault")
  void testUpdateImageToDefault(MultipartFile file, String emptyProfileLink) {
    // given
    testUser.setProfileImage(testProfileLink);
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage(emptyProfileLink)
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(false);
    when(amazonS3Uploader.getDefaultProfileImage()).thenReturn(defaultImageLink);

    // when
    userService.updateUserInfo(testUser, request, file);

    // then
    verify(userRepository).save(testUser);
    verify(amazonS3Remover).removeFile(testProfileLink, PROFILE_DIRECTORY_NAME);
    assertThat(testUser).hasFieldOrPropertyWithValue("profileImage", defaultImageLink);
  }

  @ParameterizedTest
  @DisplayName("유저 정보 수정 : A이미지 -> A이미지(파일X)")
  @MethodSource("emptyFileParameter")
  void testUpdateImageToImage(MultipartFile file) {
    // given
    testUser.setProfileImage(testProfileLink);
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage(testProfileLink)
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(false);
    when(amazonS3Uploader.getDefaultProfileImage()).thenReturn(defaultImageLink);
    // when
    userService.updateUserInfo(testUser, request, file);

    // then
    verify(userRepository).save(testUser);
    assertThat(testUser).hasFieldOrPropertyWithValue("profileImage", testProfileLink);
  }

  @Test
  @DisplayName("유저 정보 수정 : A이미지 -> B이미지(파일O)")
  void testUpdateImageToNewImage() throws IOException {
    // given
    testUser.setProfileImage(testProfileLink);
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage("")
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(false);
    when(amazonS3Uploader.getDefaultProfileImage()).thenReturn(defaultImageLink);
    when(amazonS3Uploader.upload(eq(testProfileFile), eq(PROFILE_DIRECTORY_NAME))).thenReturn(
        "new link");
    // when
    userService.updateUserInfo(testUser, request, testProfileFile);

    // then
    verify(amazonS3Uploader).upload(eq(testProfileFile),
        eq(PROFILE_DIRECTORY_NAME));
    verify(amazonS3Remover).removeFile(testProfileLink, PROFILE_DIRECTORY_NAME);
    verify(userRepository).save(testUser);
    assertThat(testUser).hasFieldOrPropertyWithValue("profileImage", "new link");
  }

  @ParameterizedTest
  @DisplayName("유저 정보 수정 : 기본이미지도 아니고 기존이미지 링크도 아닌 잘못된 프로필 링크")
  @MethodSource("emptyFileParameter")
  void testWrongProfileLink(MultipartFile file) {
    // given
    testUser.setProfileImage(testProfileLink);
    UserUpdateRequest request = UserUpdateRequest.builder()
        .nickname(testUser.getNickname())
        .profileImage("anotherProfileLink")
        .build();
    when(userRepository.existsByNicknameExceptId(testUser.getId(),
        request.getNickname())).thenReturn(false);
    when(amazonS3Uploader.getDefaultProfileImage()).thenReturn(defaultImageLink);

    // when then
    assertThatThrownBy(() -> userService.updateUserInfo(testUser, request, file))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage(USER_PROFILE_NOT_MATCHED.getMessage());
  }

  @Test
  @DisplayName("유저 비밀번호 변경")
  void testUpdatePassword() {
    // given
    PasswordUpdateRequest request = new PasswordUpdateRequest(testPassword, "test2345!");
    // when
    userService.updatePassword((LocalUser) testUser, request);
    // then
    verify(userRepository).save(testUser);
  }

  @ParameterizedTest
  @MethodSource("invalidPasswordParameter")
  @DisplayName("유저 비밀번호 변경 : 비밀번호 규칙 위반")
  void testUpdateInvalidPassword(String invalidPassword) {
    // given
    PasswordUpdateRequest request = new PasswordUpdateRequest(testPassword, invalidPassword);
    // when then
    assertThatThrownBy(() -> userService.updatePassword((LocalUser)testUser, request))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessage(INVALID_INPUT_VALUE.getMessage());
  }

  @Test
  @DisplayName("소셜로그인 회원가입 테스트")
  void testOAuthSignUp() {
    //given
    Map<String, Object> attributes = new HashMap<>();
    Map<String, Object> properties = Map.of(
        "nickname", "김승은",
        "profile_image", "testUrl"
    );
    Map<String, Object> accountInfo = Map.of(
        "email", "julie0005@ajou.ac.kr"
    );
    attributes.put("properties", properties);
    attributes.put("kakao_account", accountInfo);
    attributes.put("id", "12345678");
    String provider = "kakao";
    OAuth2User oAuth2User = new DefaultOAuth2User(List.of(Role.toGrantedAuthority(new Role(Authority.USER))), attributes, "id");
    when(userRepository.findByProviderAndProviderId(eq(provider), eq(oAuth2User.getName()))).thenReturn(Optional.empty());
    when(roleRepository.findByAuthority(Authority.USER)).thenReturn(Optional.of(userRole));

    // when
    userService.oauthSignUp(oAuth2User, provider);

    // then
    verify(userRepository).findByProviderAndProviderId(provider, oAuth2User.getName());
    verify(roleRepository).findByAuthority(Authority.USER);
    verify(userRepository).save(any());
  }


  private static Stream<Arguments> errorLoginParameter() {
    return Stream.of(
        Arguments.of(testEmail, null),
        Arguments.of(null, testPassword),
        Arguments.of(null, null)
    );
  }

  private static Stream<Arguments> emptyFileParameterWithDefault() {
    MultipartFile noSizeFile = new MockMultipartFile("testImage",
        "testImage.png",
        MediaType.MULTIPART_FORM_DATA_VALUE,
        new byte[0]);
    return Stream.of(
        Arguments.of(noSizeFile, ""),
        Arguments.of(null, ""),
        Arguments.of(noSizeFile, null),
        Arguments.of(null, null),
        Arguments.of(noSizeFile, defaultImageLink),
        Arguments.of(null, defaultImageLink)
    );
  }

  private static Stream<MultipartFile> emptyFileParameter() {
    MultipartFile noSizeFile = new MockMultipartFile("testImage",
        "testImage.png",
        MediaType.MULTIPART_FORM_DATA_VALUE,
        new byte[0]);
    return Stream.of(
        noSizeFile,
        null
    );
  }

  private static Stream<String> invalidPasswordParameter() {
    return Stream.of(
        "!lt8",
        "onlyenglish",
        "noSpecial1"
    );
  }
}