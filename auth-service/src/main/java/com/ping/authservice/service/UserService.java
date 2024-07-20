package com.ping.authservice.service;

import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.dto.ProfileResponse;

import com.ping.authservice.model.Otp;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.FollowRepository;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.util.BasicResponse;
import com.ping.authservice.util.EmailUtil;
import com.ping.authservice.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private OtpUtil otpUtil;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private FollowRepository followRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).
                orElseThrow(()-> new UsernameNotFoundException("Username Not Found"));
    }



//    public SendOtpResponse sendOtpToMail(RegistrationRequest regRequest) {
//        String otp = otpUtil.generateOtp();
//        try {
//            emailUtil.sendOtpToEmail(regRequest.getEmail(), otp);
//            return new SendOtpResponse(true, "success");
//        } catch (MailException e) {
//            // Handle specific mail sending exceptions
//            return new SendOtpResponse(false, "Error sending OTP: " + e.getMessage());
//        } catch (Exception e) {
//            // Catch other unexpected exceptions
//            return new SendOtpResponse(false, "An error occurred: " + e.getMessage());
//        }
//    }



    public User userExists(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if(existingUser.isPresent()){
            return userRepository.findByEmail(email).get();
        }
        return null;
    }


    public boolean userNameExists(String userName) {
        Optional<User> existingUser = userRepository.findByAccountName(userName);
        return existingUser.isPresent();
    }

    public User userNameExist(String userName) {
        Optional<User> existingUser = userRepository.findByAccountName(userName);
        if(existingUser.isPresent()){
            return userRepository.findByAccountName(userName).get();
        }
        return null;
    }

    public ProfileResponse getUserDetails(int userId) {

//        User user = userRepository.findByEmail(email).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("user not found"));
        ProfileResponse profileResponse = new ProfileResponse();
        profileResponse.setFullName(user.getFullName());
        profileResponse.setEmail(user.getEmail());
        profileResponse.setAccountName(user.getAccountName());
        profileResponse.setBio(user.getBio());
        profileResponse.setImage(user.getImageUrl());
        profileResponse.setFollowers(followRepository.findByFollowingId(userId));
        profileResponse.setFollowing(followRepository.findByFollowerId(userId));
        profileResponse.setPrivate(user.isPrivate());
        profileResponse.setSubscribed(user.isSubscribed());
        profileResponse.setSubscriptionEndDate(user.getSubscriptionEndDate());
        return profileResponse;
    }

    public BasicResponse updateUserDetails(int userId, ProfileResponse request) {
        try {
            User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("user not found"));

            // Check if the new email belongs to another user
            User userWithEmail = userExists(request.getEmail());
            if (userWithEmail != null && userWithEmail.getId() != userId) {
                return BasicResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("Email already exists")
                        .description("There is conflict with an already existing email")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // Check if the new username belongs to another user
            User userWithUsername = userNameExist(request.getAccountName());
            if (userWithUsername != null && userWithUsername.getId() != userId) {
                return BasicResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message("Username already exists")
                        .description("There is conflict with an already existing username")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

            // Edit user details
            user.setEmail(request.getEmail());
            user.setAccountName(request.getAccountName());
            user.setFullName(request.getFullName());
            user.setBio(request.getBio() != null ? request.getBio() : "");
            userRepository.save(user);
            return BasicResponse.builder()
                    .status( HttpStatus.OK.value())
                    .message("Success")
                    .description("User details updates successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            return BasicResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .description("server side error")
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }


    public void saveImageUrl(String imageUrl,Integer id) {
        try {
            User editUser = userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("user not found"));

            editUser.setImageUrl(imageUrl);
            userRepository.save(editUser);
        } catch (UsernameNotFoundException ex) {
            throw new UsernameNotFoundException(ex.getMessage());
        }
    }

    public User findUserByHeader(String authHeader) {
        String jwt = "";
        String userEmail ="";
        User user = new User();
        if(authHeader != null ) {
            jwt = authHeader.substring(7);
            userEmail = jwtService.extractUsername(jwt);
            System.out.println("userEMail from auth =" +userEmail);
            user = userRepository.findByEmail(userEmail).orElseThrow(()->new UsernameNotFoundException("user not found"));
            System.out.println("user from auth =" +user);
            return user;
        }
       return user;
    }

    public BasicResponse handlePrivacy(String header) {
        try {
            User user = findUserByHeader(header);
            user.setPrivate(!user.isPrivate());
            userRepository.save(user);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("success")
                    .description("privacy changed successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<User> getAllUsersBasedOnSearch(String search) {
        if(Objects.equals(search, "")) {
            return userRepository.findAll();
        }
        return userRepository.findByAccountNameContainingIgnoreCase(search);
    }
}
