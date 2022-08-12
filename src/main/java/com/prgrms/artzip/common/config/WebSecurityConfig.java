package com.prgrms.artzip.common.config;

import static com.prgrms.artzip.common.Authority.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgrms.artzip.common.ErrorCode;
import com.prgrms.artzip.common.ErrorResponse;
import com.prgrms.artzip.common.filter.ExceptionHandlerFilter;
import com.prgrms.artzip.common.jwt.Jwt;
import com.prgrms.artzip.common.jwt.JwtAuthenticationFilter;
import com.prgrms.artzip.common.jwt.JwtAuthenticationProvider;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.common.oauth.OAuth2AuthenticationSuccessHandler;
import com.prgrms.artzip.user.service.UserService;
import com.prgrms.artzip.user.service.UserUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final JwtConfig jwtConfig;

  @Bean
  @Qualifier("accessJwt")
  public Jwt accessJwt() {
    return new Jwt(
        jwtConfig.getIssuer(),
        jwtConfig.getClientSecret(),
        jwtConfig.getAccessToken().getExpirySeconds());
  }

  @Bean
  @Qualifier("refreshJwt")
  public Jwt refreshJwt() {
    return new Jwt(
        jwtConfig.getIssuer(),
        jwtConfig.getClientSecret(),
        jwtConfig.getRefreshToken().getExpirySeconds());
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService,
      UserService userService) {
    return new JwtAuthenticationProvider(jwtService, userService);
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
    return (request, response, e) -> {
      response.setStatus(HttpStatus.FORBIDDEN.value());
      String json = objectMapper.writeValueAsString(ErrorResponse.of(ErrorCode.ACCESS_DENIED));
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      PrintWriter writer = response.getWriter();
      writer.write(json);
      writer.flush();
    };
  }

  @Bean
  public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
    return (request, response, e) -> {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      String json = objectMapper.writeValueAsString(
          ErrorResponse.of(ErrorCode.UNAUTHENTICATED_USER));
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      PrintWriter writer = response.getWriter();
      writer.write(json);
      writer.flush();
    };
  }

  @Bean
  public ExceptionHandlerFilter exceptionHandlerFilter(ObjectMapper objectMapper) {
    return new ExceptionHandlerFilter(objectMapper);
  }

  @Bean
  public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler(JwtService jwtService, UserService userService, ObjectMapper objectMapper) {
    return new OAuth2AuthenticationSuccessHandler(jwtService, userService, objectMapper);
  }

  public WebSecurityConfig(JwtConfig jwtConfig) {
    this.jwtConfig = jwtConfig;
  }

  public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService,
      UserUtilService userUtilService) {
    return new JwtAuthenticationFilter(jwtConfig.getAccessToken().getHeader(), jwtService,
        userUtilService);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService,
      UserUtilService userUtilService, ObjectMapper objectMapper,
      ExceptionHandlerFilter exceptionHandlerFilter, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler) throws Exception {
    http
        .cors()
        .and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        .antMatchers(
            "/api/v1/users/me/**",
            "/api/v1/exhibitions/**/likes",
            "api/v1/users/logout").hasAnyAuthority(USER.name(), ADMIN.name())
        .antMatchers(HttpMethod.POST,
            "/api/v1/reviews", "/api/v1/comments/**", "/api/v1/reviews/**/comments").hasAnyAuthority(USER.name(), ADMIN.name())
        .antMatchers(HttpMethod.PATCH,
            "/api/v1/reviews/**", "/api/v1/reviews/**/like", "/api/v1/comments/**")
        .hasAnyAuthority(USER.name(), ADMIN.name())
        .antMatchers(HttpMethod.DELETE,
            "/api/v1/reviews/**", "/api/v1/comments/**").hasAnyAuthority(USER.name(), ADMIN.name())
        .anyRequest().permitAll()
        .and()
        .oauth2Login()
        .authorizationEndpoint()
        .baseUri("/oauth/login")
        .and()
        .successHandler(oAuth2AuthenticationSuccessHandler)
        .and()
        .exceptionHandling()
        .accessDeniedHandler(accessDeniedHandler(objectMapper))
        .authenticationEntryPoint(authenticationEntryPoint(objectMapper))
        .and()
        .addFilterBefore(jwtAuthenticationFilter(jwtService, userUtilService),
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class);
    return http.build();
  }
}
