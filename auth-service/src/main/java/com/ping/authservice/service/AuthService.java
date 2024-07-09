package com.ping.authservice.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.CustomBadCredentialException;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UserBlockedException;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.dto.AuthRequest;
import com.ping.authservice.dto.AuthResponse;
import com.ping.authservice.dto.RegistrationRequest;
import com.ping.authservice.model.Otp;
import com.ping.authservice.model.Role;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.OtpRepository;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.util.BasicResponse;
import com.ping.authservice.util.EmailUtil;
import com.ping.authservice.util.OtpUtil;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private OtpRepository otpRepository;

//    @Autowired
    private RegistrationRequest registrationRequest;

    private final Map<String, RegistrationRequest> userDetailsMap = new HashMap<>();


    public AuthResponse authenticateUser(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("user not found"));

            if(user.isBlocked()) {
                throw new UserBlockedException("account is blocked");
            }

            String jwtToken = jwtService.generateToken(authRequest.getEmail());
            return AuthResponse.builder()
                    .user(user)
                    .jwtToken(jwtToken)
                    .build();
        } catch (BadCredentialsException e) {
            throw new CustomBadCredentialException("email or password is incorrect");
        } catch (UserBlockedException e) {
            throw new UserBlockedException(e.getMessage());
        }
        catch (Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }


    public void saveUserDetails(RegistrationRequest regRequest) {
        if(userService.userExists(regRequest.getEmail()) != null){
            throw new BadRequestException("User already exists");
        }

        var user = User.builder()
                .fullName(regRequest.getFullName())
                .accountName(regRequest.getAccountName())
                .email(regRequest.getEmail())
                .password(passwordEncoder.encode(regRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }

    public BasicResponse sendOtpAndSaveUser(RegistrationRequest regRequest) {

        userDetailsMap.put("key",regRequest);
        String otp = otpUtil.generateOtp();

        // Verify that user already exists or not
        if(userService.userExists(regRequest.getEmail()) != null){
//            return new SendOtpResponse(false, "Email already exists");
            return BasicResponse.builder()
                    .status( HttpStatus.CONFLICT.value ( ))
                    .message("Email already exists")
                    .description("There is conflict with already existing email")
                    .timestamp(LocalDateTime.now())
                    .build();
        }


        // Verifying that UserName already exists or not
        if(userService.userNameExists(regRequest.getAccountName())){
//            return new SendOtpResponse(false, "Username already exists");
            return BasicResponse.builder()
                    .status( HttpStatus.CONFLICT.value ( ))
                    .message("Username already exists")
                    .description("There is conflict with already existing username")
                    .timestamp(LocalDateTime.now())
                    .build();
        }


        try {
            emailUtil.sendOtpToEmail(regRequest.getEmail(), otp);
            Otp otpDetails = otpRepository.findByEmail(regRequest.getEmail());
            if(otpDetails == null) {
                var newOtp = Otp.builder()
                        .otpGenerated(otp)
                        .email(regRequest.getEmail())
                        .otpGeneratedAt(LocalDateTime.now())
                        .build();
                otpRepository.save(newOtp);
            } else {
                otpDetails.setOtpGenerated(otp);
                otpDetails.setOtpGeneratedAt(LocalDateTime.now());
                otpRepository.save(otpDetails);
            }
            // Gives success message
//            return new SendOtpResponse(true, "success");
            return BasicResponse.builder()
                    .status( HttpStatus.OK.value())
                    .message("Success")
                    .description("First part of account creation is successful")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (MailException e) {
            // Handle specific mail sending exceptions
//            return new SendOtpResponse(false, "Error sending OTP: " + e.getMessage());
            return BasicResponse.builder()
                    .status( HttpStatus.CONFLICT.value())
                    .message(e.getMessage())
                    .description("Some Error occurred while sending OTP")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            // Catch other unexpected exceptions
//            return new SendOtpResponse(false, "An error occurred: " + e.getMessage());
            return BasicResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .description("server side error")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    public BasicResponse verifyOtp(String otp) {
        try {
            RegistrationRequest regRequest = userDetailsMap.get("key");
            if (regRequest == null) {
                return BasicResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("Failed to get your details,register again.")
                        .description("Some Error occurred while accessing you details, please register again")
                        .timestamp(LocalDateTime.now())
                        .build();  // User not found (or key might be wrong)
            }
            System.out.println(regRequest.getEmail() + "=================================");
            Otp otpDetails = otpRepository.findByEmail(regRequest.getEmail());
            if (otpDetails != null && otpDetails.getOtpGenerated().equals(otp) && Duration.between(otpDetails.getOtpGeneratedAt(),
                    LocalDateTime.now()).getSeconds() < (120)) {
//            userDetailsMap.remove("key");
                createAccount(regRequest);
                return BasicResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("OTP verification successful!")
                        .description("OTP verified successfully ,created your account")
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("please signup again");
        }
        return BasicResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Invalid OTP. Please try again.")
                .description("server side error")
                .timestamp(LocalDateTime.now())
                .build();
    }


    private void createAccount (RegistrationRequest regRequest) {
        // Save user details
        var user = User.builder()
                .fullName(regRequest.getFullName())
                .accountName(regRequest.getAccountName())
                .email(regRequest.getEmail())
                .password(passwordEncoder.encode(regRequest.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
    }


    public BasicResponse resendOtp() {
        try {
            String otp = otpUtil.generateOtp();
            RegistrationRequest regRequest = userDetailsMap.get("key");
            if(regRequest == null) {
                throw new RuntimeException("Failed to get your details,register again.");
            }
            emailUtil.sendOtpToEmail(regRequest.getEmail(), otp);
            Otp otpDetails = otpRepository.findByEmail(regRequest.getEmail());
            if(otpDetails == null) {
                throw new RuntimeException("please signup once again");
            } else {
                otpDetails.setOtpGenerated(otp);
                otpDetails.setOtpGeneratedAt(LocalDateTime.now());
                otpRepository.save(otpDetails);
            }
            // Gives success message
//            return new SendOtpResponse(true, "success");
            return BasicResponse.builder()
                    .status( HttpStatus.OK.value())
                    .message("Success")
                    .description("OTP successfully resented")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (MailException e) {
            // Handle specific mail sending exceptions
//            return new SendOtpResponse(false, "Error sending OTP: " + e.getMessage());
            return BasicResponse.builder()
                    .status( HttpStatus.CONFLICT.value())
                    .message(e.getMessage())
                    .description("Some Error occurred while sending OTP")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            // Catch other unexpected exceptions
//            return new SendOtpResponse(false, "An error occurred: " + e.getMessage());
            return BasicResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .description("server side error")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    public Object saveUserByGoogleDetails(GoogleIdToken.Payload payload) {
        try {

            if(userService.userExists(payload.getEmail()) == null){
//            return new SendOtpResponse(false, "Email already exists");
//                return BasicResponse.builder()
//                        .status( HttpStatus.CONFLICT.value ( ))
//                        .message("Email already exists")
//                        .description("There is conflict with already existing email")
//                        .timestamp(LocalDateTime.now())
//                        .build();
                saveUserDetails(payload);
            }
                User user = userRepository.findByEmail(payload.getEmail()).orElseThrow(() -> new UsernameNotFoundException("user not found,try again"));


                String jwtToken = jwtService.generateToken(payload.getEmail());
                return AuthResponse.builder()
                        .user(user)
                        .jwtToken(jwtToken)
                        .build();
        } catch (BadCredentialsException e) {
            throw new CustomBadCredentialException("email or password is incorrect");
        } catch (UserBlockedException e) {
            throw new UserBlockedException(e.getMessage());
        }
        catch (Exception e){
            throw new UsernameNotFoundException(e.getMessage());
        }
    }

    private void saveUserDetails(GoogleIdToken.Payload payload) {
        User user = User.builder()
                .fullName((String) payload.get("name"))
                .accountName((String) payload.get("name"))
                .email(payload.getEmail())
                .role(Role.USER)
                .password(passwordEncoder.encode(payload.getSubject()))
                .imageUrl((String) payload.get("picture"))
                .build();

        userRepository.save(user);
    }

    public void isSubscriptionValid(User user) {
        LocalDateTime endDate = user.getSubscriptionEndDate();
        LocalDateTime currentDate = LocalDateTime.now(); // Get current date and time

        if (currentDate.isAfter(endDate)) {
            user.setSubscribed(false);
            userRepository.save(user);
        }
    }

    public List<User> getAllUsersById(List<Integer> userIds) {
        List<User> userList = new ArrayList<>();
        for (Integer id: userIds) {
            Optional<User> user = userRepository.findById(id);
            user.ifPresent(userList::add);
        }
//      return  userRepository.findByIdIn(userIds);
        return userList;
    }
}
