package com.example.springproject3.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class JWTUtil {
    private static final String SECRET_KEY="foobar_123456789foobar_123456789foobar_123456789foobar_123456789";

    public String issueToken(String subject){
        return issueToken(subject,Map.of());
    }

    public String issueToken(String subject,String ...scopes){
        return issueToken(subject,Map.of("scopes",scopes));
    }
    public String issueToken(String subject, Map<String,Object> claims){
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("kevin")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
        return token;
    }
    private Key getSigningKey(){
      return  Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String getSubject(String token){
        return getClaims(token).getSubject();
    }
    public Claims getClaims(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }

    public boolean isTokenValid(String jwt,String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        return getClaims(jwt).getExpiration().before(Date.from(Instant.now()));
    }


    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject,Map.of("scopes",scopes));

    }
}
