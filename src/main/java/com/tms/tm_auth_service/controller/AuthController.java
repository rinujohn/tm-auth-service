package com.tms.tm_auth_service.controller;

import com.tms.tm_auth_service.dto.request.LoginRequest;
import com.tms.tm_auth_service.dto.request.RefreshTokenRequest;
import com.tms.tm_auth_service.dto.response.LoginResponse;
import com.tms.tm_auth_service.dto.response.RefreshTokenResponse;
import com.tms.tm_auth_service.service.AuthenticationService;
import com.tms.tm_auth_service.service.JwtService;
import com.tms.tm_auth_service.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome page";
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){

//        Authentication authentication =
//                authenticationManager.authenticate(
//                        new UsernamePasswordAuthenticationToken(
//                                request.email(),
//                                request.password()
//                        )
//                );
        System.out.println("Controller reached");

        LoginResponse response = authenticationService.login(request);

        return ResponseEntity.ok(response);

    }
//    @GetMapping("/encode")
//    public String encode() {
//        return passwordEncoder.encode("test");
//    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest request){
        String refreshToken = request.refreshToken();

            RefreshTokenResponse response = refreshTokenService.validateRefreshToken(refreshToken);
           return ResponseEntity.ok(response);


    }
}
