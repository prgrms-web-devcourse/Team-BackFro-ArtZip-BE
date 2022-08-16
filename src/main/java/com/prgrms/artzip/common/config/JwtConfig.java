package com.prgrms.artzip.common.config;

import com.prgrms.artzip.common.jwt.Jwt;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "spring.jwt")
@Getter
@Setter
public class JwtConfig {
    private String issuer;
    private String clientSecret;
    private Token accessToken;
    private Token refreshToken;
    private String blackListPrefix;
    @Getter
    @Setter
    public static class Token {
        private String header;
        private int expirySeconds;

        @Override
        public String toString() {
            return "header: "+header+" expirySeconds: "+expirySeconds;
        }
    }

    @Bean
    @Qualifier("accessJwt")
    public Jwt accessJwt() {
        return new Jwt(
            this.issuer,
            this.clientSecret,
            this.accessToken.expirySeconds);
    }

    @Bean
    @Qualifier("refreshJwt")
    public Jwt refreshJwt() {
        return new Jwt(
            this.issuer,
            this.clientSecret,
            this.refreshToken.expirySeconds);
    }
}
