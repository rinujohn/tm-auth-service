package com.tms.tm_auth_service.service;

import com.tms.tm_auth_service.dto.response.RefreshTokenResponse;
import com.tms.tm_auth_service.entity.RefreshToken;
import com.tms.tm_auth_service.entity.User;
import com.tms.tm_auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service

public class RefreshTokenService {

    private final JwtService jwtService;

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    private final long refreshTokenExpirationDays;

    public RefreshTokenService(
            JwtService jwtService, RefreshTokenRepository refreshTokenRepository,
            @Value("${security.jwt.refresh-token-expiration-days:7}")
            long refreshTokenExpirationDays) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    @Transactional
    public String createRefreshToken(User user) {

        // 1. Generate a cryptographically secure random token
        String rawToken = generateSecureToken();

        // 2. Hash the token before storing it
        String tokenHash = hashToken(rawToken);

        // 3. Create entity
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setTokenHash(tokenHash);
        refreshToken.setUser(user);
        refreshToken.setCreatedAt(Instant.now());
        refreshToken.setExpiresAt(
                Instant.now()
                        .plus(refreshTokenExpirationDays, ChronoUnit.DAYS)
        );
        refreshToken.setRevoked(false);

        // 4. Save hash to database
        refreshTokenRepository.save(refreshToken);

        // 5. Return raw token to client
        return rawToken;
    }

    private String generateSecureToken() {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            token.getBytes(StandardCharsets.UTF_8)
                    );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm not available", e
            );
        }
    }

    @Transactional
    public RefreshTokenResponse validateRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid refresh token"
                                )
                        );

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException(
                    "Refresh token has been revoked"
            );
        }

        if (refreshToken.getExpiresAt()
                .isBefore(Instant.now())) {

            throw new IllegalArgumentException(
                    "Refresh token has expired"
            );
        }
        /*
        get user details

        revoke current token
        generate new jwt token
        genrate new refresh token

         */
        User user = refreshToken.getUser();
        String jwtToken = jwtService.generateToken(user);
        this.revokeToken(rawToken);
        String newRefreshToken =  this.createRefreshToken(user);
        return  new RefreshTokenResponse(jwtToken,newRefreshToken);

    }


    @Transactional
    public void revokeToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken token =
                refreshTokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new RuntimeException("Token not found"));

        System.out.println("Before revoked: " + token.isRevoked());
        System.out.println("Before revokedAt: " + token.getRevokedAt());

        token.setRevoked(true);
        token.setRevokedAt(Instant.now());

        System.out.println("After revoked: " + token.isRevoked());
        System.out.println("After revokedAt: " + token.getRevokedAt());

        refreshTokenRepository.saveAndFlush(token);
    }
//    @Transactional
//    public void revokeToken(String rawToken) {
//        String tokenHash = hashToken(rawToken);
//        System.out.println("raw token "+rawToken);
//        System.out.println("token hash"+tokenHash);
//        refreshTokenRepository
//                .findByTokenHash(tokenHash)
//                .ifPresent(token -> {
//                    System.out.println("Token FOUND: " + token.getId());
//
//                    token.setRevoked(true);
//                    token.setRevokedAt(Instant.now());
//
//                    refreshTokenRepository.save(token);
//                    System.out.println("Token revoked");
//                });
//
//
//    }

}