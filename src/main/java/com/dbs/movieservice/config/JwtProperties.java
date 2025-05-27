package com.dbs.movieservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secret = "defaultSecretKeyForJWTAuthenticationInMovieServiceApplication";
    private long expirationMs = 86400000; // 1 day
    private String issuer = "movie-service";
} 