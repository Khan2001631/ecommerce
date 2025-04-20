package com.khan.EComm.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private static final String SECRET_KEY_STRING="khan#123ssadasdasdsdasdasdasdasdasdasdasdasdasdasdasd";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    public String generateToken(String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+ 1000 * 300))
                .signWith(SECRET_KEY)
                .compact();
        System.out.println("Token is generated.");
        return token;
    }

    public boolean validateToken(String token) {
        try{
            // The SECRET_KEY that was used during encryption is now being used for decryption
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        }
        catch(ExpiredJwtException e) {
            System.out.println("Token expired"+e.getMessage());
            return false;
        }
        catch(SignatureException e) {
            System.out.println("Invalid JWT Signature"+e.getMessage());
            return false;
        }
        catch(Exception e) {
            System.out.println("JWT exception"+e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
