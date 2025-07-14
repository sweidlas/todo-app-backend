package com.example.graphqlserver.controller;

import static com.example.graphqlserver.service.Utilities.mapToUserResponse;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.graphqlserver.auth_dto.LoginUserDto;
import com.example.graphqlserver.auth_dto.RegisterUserDto;
import com.example.graphqlserver.auth_dto.ResendVerificationCodeDto;
import com.example.graphqlserver.auth_dto.VerifyUserDto;
import com.example.graphqlserver.model.UserPrincipal;
import com.example.graphqlserver.responses.LoginResponse;
import com.example.graphqlserver.service.AuthenticationService;
import com.example.graphqlserver.service.JwtUtil;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserPrincipal> register(@RequestBody RegisterUserDto registerUserDto) {
        UserPrincipal registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        UserPrincipal authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime(),
                mapToUserResponse(authenticatedUser));
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        authenticationService.verifyUser(verifyUserDto);
        Map<String, String> success = new HashMap<>();
        success.put("success", "Account verified successfully");
        return ResponseEntity.ok(success);
    }

    @GetMapping("/verifyEmail/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable String token) {
        try {
            authenticationService.verifyEmail(token);
            String htmlResponse = "<!DOCTYPE html><html><head><title>Email Verification</title></head><body><h1>Email change successful</h1></body></html>";
            return ResponseEntity.ok().header("Content-Type", "text/html").body(htmlResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendVerificationCodeDto resendVerificationCodeDto) {
        // TODO: check if account must be authenticated

        authenticationService.resendVerificationCode(resendVerificationCodeDto.email());
        Map<String, String> success = new HashMap<>();
        success.put("success", "Verification code sent");

        return ResponseEntity.ok(success);
    }
}