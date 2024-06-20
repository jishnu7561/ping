package com.ping.authservice.controller.user;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.dto.*;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.service.AuthService;
import com.ping.authservice.service.JwtService;
import com.ping.authservice.service.UserService;
import com.ping.authservice.util.BasicResponse;
import com.ping.authservice.util.GoogleTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/user/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        return ResponseEntity.ok(authService.authenticateUser(authRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<BasicResponse> register(@RequestBody RegistrationRequest regRequest) {

        System.out.println("called the register method" + regRequest);
        return ResponseEntity.ok(authService.sendOtpAndSaveUser(regRequest));
    }

    @PostMapping("/otpVerification")
    public ResponseEntity<BasicResponse>  verifyOtp(@RequestBody OtpRequest otpRequest) {
        return ResponseEntity.ok(authService.verifyOtp(otpRequest.getOtp()));
    }

    @GetMapping("/resend-otp")
    public ResponseEntity<BasicResponse> resendOtp() {
        return ResponseEntity.ok(authService.resendOtp());
    }

    @GetMapping("/status/{userName}")
    public ResponseEntity<String> getUserStatus(@PathVariable String userName) {
        System.out.println("email=======  " +userName);
        System.out.println("callingg+++++++++++++++++++++++");
        User user = userRepository.findByEmail(userName).orElseThrow(()->new UsernameNotFoundException("user not found"));
        return ResponseEntity.ok(user.isBlocked() ? "BLOCKED" : "ACTIVE");
    }

    @GetMapping("/isUserExist/{id}")
    public ResponseEntity<User> getUserIfExist(@PathVariable Integer id) {
        Optional<User> user = userRepository.findById(id);
        return ResponseEntity.ok(user.get());
    }

    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(@RequestHeader("Authorization") String header){
        System.out.println("header ="+header);
        return ResponseEntity.ok(userService.findUserByHeader(header));
    }

    @PostMapping("/google")
    public ResponseEntity<?> authenticateWithGoogle(@RequestBody GoogleAuthRequest googleAuthRequest) throws GeneralSecurityException, IOException {
        String idToken = googleAuthRequest.getToken();
        GoogleIdToken.Payload payload = googleTokenVerifier.validateToken(idToken);

        if (payload == null) {
            return ResponseEntity.badRequest().body("please try again, something went wrong!!");
        }

        String userId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        // Here you would handle user registration or login
        System.out.println("Google user ID: " + userId);
        System.out.println("Google user Email: " + email);
        System.out.println("Google user Name: " + name);
        System.out.println(payload);

        return ResponseEntity.ok(authService.saveUserByGoogleDetails(payload));
    }


}

class GoogleAuthRequest {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


}
