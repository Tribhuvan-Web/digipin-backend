package com.hackathon.resolutionconsent.digipin.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String getSecret() {
        return secret;
    }

    public boolean isAuthTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            String tokenType = claims.get("type", String.class);
            return "auth".equals(tokenType) && !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Long extractUserIdFromAuthToken(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
    }

    public String getEmailFromJWt(String token){
        Claims claims = Jwts.parser().verifyWith((SecretKey) key())
        .build().parseSignedClaims(token).getPayload();

        return claims.getId();
    }

    public String getNameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public String generateToken(UserDetailsImplement userDetails) {
        String email = userDetails.getEmail();
        String phone = userDetails.getPhone();
        String displayName = userDetails.getUsername();
        Long userId = userDetails.getId();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String subject = (email != null && !email.isEmpty()) ? email : phone;

        return Jwts.builder()
                .subject(subject)
                .claim("roles", roles)
                .claim("displayName", displayName)
                .claim("email", email)
                .claim("phone", phone)
                .claim("userId", userId)
                .claim("type", "auth")
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + expirationTime))
                .signWith(key())
                .compact();
    }

    public String generateConsentToken(Long userId, Long digitalAddressId,
            String purpose, int validityHours) {
        long consentExpiration = validityHours * 60 * 60 * 1000L;

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userId", userId)
                .claim("digitalAddressId", digitalAddressId)
                .claim("purpose", purpose)
                .claim("type", "consent")
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + consentExpiration))
                .signWith(key())
                .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {

        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}