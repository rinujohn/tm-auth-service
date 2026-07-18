package com.tms.tm_auth_service.service;

import com.tms.tm_auth_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {


    private final String SECRET =
            "8f3a9c7d6e5b4a2918273645546372819f8e7d6c5b4a39281726354455667788";


    public String generateToken(User user) {


        return Jwts.builder()

                .setSubject(user.getUsername())

                .claim(
                        "roles",
                        user.getAuthorities()
                )

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 1000 * 60 * 15)
                )

                .signWith(
                        Keys.hmacShaKeyFor(
                                SECRET.getBytes())
                )

                .compact();
    }
}