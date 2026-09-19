package com.jinanmuseum.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
public class JwtConfig {

    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expire-hours}")
    private int jwtExpireHours;

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(jwtSecret, jwtExpireHours);
    }
}