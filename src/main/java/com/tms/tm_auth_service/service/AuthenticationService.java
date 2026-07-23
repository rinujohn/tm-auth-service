package com.tms.tm_auth_service.service;

import com.tms.tm_auth_service.dto.request.LoginRequest;
import com.tms.tm_auth_service.dto.response.LoginResponse;
import com.tms.tm_auth_service.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest request) {
        System.out.println("inside auth service");
    System.out.println("length of email in request"+request.email().length());
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.email(),
                                request.password()
                        )
                );


        User user =
                (User) authentication.getPrincipal();


        String token =
                jwtService.generateToken(user);

        String refreshToken = refreshTokenService.createRefreshToken(user);

        return new LoginResponse(token,refreshToken);
    }
    @Transactional
    public void logout(String rawRefreshToken){
        refreshTokenService.revokeToken(rawRefreshToken);
    }
}