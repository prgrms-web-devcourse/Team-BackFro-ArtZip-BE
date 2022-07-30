package com.prgrms.artzip.common.config;

import com.prgrms.artzip.common.jwt.Jwt;
import com.prgrms.artzip.common.jwt.JwtAuthenticationFilter;
import com.prgrms.artzip.common.jwt.JwtAuthenticationProvider;
import com.prgrms.artzip.common.util.JwtService;
import com.prgrms.artzip.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

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
    public JwtAuthenticationProvider jwtAuthenticationProvider(JwtService jwtService, UserService userService) {
        return new JwtAuthenticationProvider(jwtService, userService);
    }

    public WebSecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtConfig.getAccessToken().getHeader(), jwtService);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/assets/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtService jwtService) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and()
                .addFilterAfter(jwtAuthenticationFilter(jwtService), SecurityContextHolderFilter.class);
        return http.build();
    }
}
