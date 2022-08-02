package com.prgrms.artzip.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
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
}
