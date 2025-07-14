package com.example.graphqlserver.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.graphqlserver.auth_dto.LoginUserDto;
import com.example.graphqlserver.auth_dto.RegisterUserDto;
import com.example.graphqlserver.auth_dto.VerifyUserDto;
import com.example.graphqlserver.exceptions.CustomException;
import com.example.graphqlserver.exceptions.ErrorCode;
import com.example.graphqlserver.model.UserPrincipal;
import com.example.graphqlserver.model.UserPrincipal.UserResponse;
import com.example.graphqlserver.model.UserSettings;
import com.example.graphqlserver.repository.UserPrincipalRepository;
import com.example.graphqlserver.repository.UserSettingsRepository;

import jakarta.mail.MessagingException;

@Service
public class AuthenticationService {

    @Autowired
    private UserPrincipalRepository userRepository;

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Autowired
    JwtUtil jwtService;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    public UserPrincipal signup(RegisterUserDto input) {
        Optional<UserPrincipal> userByEmail = userRepository.findByEmail(input.email());
        if (userByEmail.isPresent())
            throw new CustomException(ErrorCode.USER_EMAIL_EXISTS);

        Optional<UserPrincipal> userByUsername = userRepository.findByUsername(input.username());
        if (userByUsername.isPresent())
            throw new CustomException(ErrorCode.USER_NAME_EXISTS);
        GrantedAuthority userRole = new SimpleGrantedAuthority("ROLE_USER");
        UserPrincipal user = new UserPrincipal(input.username(), input.email(),
                passwordEncoder.encode(input.password()), List.of(userRole), null);
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        UserPrincipal savedUser = userRepository.save(user);
        
        // Create default UserSettings for the new user
        UserSettings defaultSettings = UserSettings.createDefault(savedUser.getId());
        userSettingsRepository.save(defaultSettings);
        
        return savedUser;
    }

    public UserPrincipal authenticate(LoginUserDto input) {
        UserPrincipal user = userRepository.findByEmail(input.email())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.isEnabled()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.email(),
                            input.password()));
        } catch (Exception error) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    public UserPrincipal checkPassword(UserPrincipal user, String password) {
        if (!user.isEnabled()) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            password));
        } catch (Exception error) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        return user;
    }

    public UserResponse verifyEmail(String jwt) {
        try {
            final String userId = jwtService.extractUsername(jwt);

            UserPrincipal user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
            if (jwtService.validateToken(jwt, user)) {
                return userService.setUserEmailAfterVerified(userId);
            } else {
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }
        } catch (Exception exception) {
            if (exception instanceof CustomException && 
                exception.getMessage().equals(ErrorCode.NO_EMAIL_TO_VERIFY.getCode())) {
                throw (CustomException) exception;
            }
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public void verifyUser(VerifyUserDto input) {
        Optional<UserPrincipal> optionalUser = userRepository.findByEmail(input.email());
        if (optionalUser.isPresent()) {
            UserPrincipal user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt() == null
                    || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new CustomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
            }
            if (user.getVerificationCode().equals(input.verificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
            }
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    public void resendVerificationCode(String email) {
        Optional<UserPrincipal> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            UserPrincipal user = optionalUser.get();
            if (user.isEnabled()) {
                throw new CustomException(ErrorCode.ACCOUNT_ALREADY_VERIFIED);
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private void sendVerificationEmail(UserPrincipal user) { // TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    public void sendEmailVerificationEmail(UserPrincipal user, String newEmail, String token) { // TODO: Update with
                                                                                                // company logo
        String subject = "Email Verification";
        UserPrincipal user2 = user;
        // String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please verify the new email address:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Email Verification:</h3>"
                + "<p style=\"font-size: 16px; margin-bottom: 20px;\">Click the button below to verify your email address:</p>"
                + "<a href=\"https://" + serverAddress + ":" + serverPort + "/auth/verifyEmail/" + token + "\" "
                + "style=\"display: inline-block; background-color: #007bff; color: white; padding: 12px 24px; "
                + "text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;\">Verify Email Address</a>"
                + "<p style=\"font-size: 14px; color: #666; margin-top: 20px;\">If the button doesn't work, copy and paste this link into your browser:</p>"
                + "<p style=\"font-size: 14px; color: #007bff; word-break: break-all;\">https://" + serverAddress + ":"
                + serverPort + "/auth/verifyEmail/"
                + token
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmail(newEmail, subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            e.printStackTrace();
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
