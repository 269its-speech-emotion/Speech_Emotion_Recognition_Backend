package com.kmits.projects.speechemotionrecognition.services;

import com.kmits.projects.speechemotionrecognition.entities.AppUser;
import com.kmits.projects.speechemotionrecognition.entities.AppUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Getter
@Setter
public class JWTService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time")
    private long jwtExpirationTime;

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(AppUser appUser){
        String email = appUser.getEmail();
        Map<String, Object> claims = new HashMap<>();
        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + getJwtExpirationTime()))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    public boolean isValidToken(String token, AppUserDetails appUserDetails){
        String username = extractUsername(token);
        return (username.equals(appUserDetails.getEmail())) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpirationTime(token).before(new Date());
    }

    public Date extractExpirationTime(String token){
        return extractClaim(token, Claims::getExpiration);
    }

}
