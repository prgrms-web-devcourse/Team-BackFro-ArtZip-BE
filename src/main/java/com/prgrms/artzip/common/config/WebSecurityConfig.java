package com.prgrms.artzip.common.config;

import com.prgrms.artzip.common.jwt.JwtAuthenticationFilter;
import com.prgrms.artzip.common.util.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.SecurityContextHolderFilter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final JwtConfig jwtConfig;

    public WebSecurityConfig(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtConfig.getAccessToken().getHeader(), jwtService);
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/assets/**", "/h2-console/**");
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
                .antMatchers("/swagger*/**").permitAll()
                .antMatchers("/api/v1/users/local/register", "/api/v1/users/local/login").permitAll()
                .antMatchers("/api/v1/users/oauth/register", "/api/v1/users/oauth/login").permitAll()
                .antMatchers("/api/v1/users/logout").permitAll()
                .antMatchers("/api/v1/users/reissue").permitAll()
                .antMatchers("/api/v1/**").hasAuthority("USER")
                .and()
                .addFilterAfter(jwtAuthenticationFilter(jwtService), SecurityContextHolderFilter.class);
        return http.build();
    }
}
