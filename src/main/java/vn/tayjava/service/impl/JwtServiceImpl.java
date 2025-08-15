package vn.tayjava.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import vn.tayjava.common.TokenType;
import vn.tayjava.service.JwtService;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j(topic = "JWT-SERVICE")
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.expiryMinutes}")
    private long expiryMinutes;

    @Value("${jwt.expiryDay}")
    private long expiryDay;

    @Value("${jwt.accessToken}")
    private String accessToken;

    @Value("${jwt.refreshToken}")
    private String refreshToken;

    @Override
    public String generateAccessToken(String username, List<String> authorities) {
        log.info("Generate access token for username {} with authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
                .compact();

//        return generateToken(claims, username);
    }

    @Override
    public String generateRefreshToken(String username, List<String> authorities) {
        log.info("Generate refresh token for username {} with authorities {}", username, authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", authorities);



//        return generateRefreshToken(claims, username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * expiryDay))
                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public String extractUsername(String token, TokenType tokenType) {
        log.info("Extract username from token {} with type {}", token, tokenType);

        return extractClaim(token, tokenType, Claims::getSubject);
    }

    private <T> T extractClaim(String token, TokenType type, Function<Claims, T> claimResolver) {
        log.info("----------[ extractClaim ]----------");
        final Claims claims = extraAllClaim(token, type);
        return claimResolver.apply(claims);
    }

    private Claims extraAllClaim(String token, TokenType type) {
        log.info("----------[ extraAllClaim ]----------");
        try {
            return Jwts.parserBuilder().setSigningKey(getKey(type)).build().parseClaimsJws(token).getBody();
        } catch (SignatureException | ExpiredJwtException e) { // Invalid signature or expired token
            throw new AccessDeniedException("Invalid token: " + e.getMessage());
        }
    }

//
//    private String generateToken(Map<String, Object> claims, String username) {
//        log.info("Generate access token for user {} with name {}", username, claims);
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * expiryMinutes))
//                .signWith(getKey(TokenType.ACCESS_TOKEN), SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    private String generateRefreshToken(Map<String, Object> claims, String username) {
//        log.info("Generate refresh token for user {} with name {}", username, claims);
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24 * expiryDay))
//                .signWith(getKey(TokenType.REFRESH_TOKEN), SignatureAlgorithm.HS256)
//                .compact();
//    }


    private Key getKey(TokenType type) {
        switch (type) {
            case ACCESS_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessToken));
            }
            case REFRESH_TOKEN -> {
                return Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshToken));
            }
            default -> throw new IllegalArgumentException("Invalid token type");
        }
    }











}
