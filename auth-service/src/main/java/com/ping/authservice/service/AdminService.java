package com.ping.authservice.service;

import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.util.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;
    public List<User> getAllUsers() {
        List<User> listOfUsers = userRepository.findAll();
        return listOfUsers;
    }



    public BasicResponse manageBlockAndUnBlock(int userId) {
        try{
            checkForBlockedUsers();
            User user = userRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("user not found"));
            if(user.isBlocked()){
                user.setBlocked(false);
            } else {
                user.setBlocked(true);
            }
            userRepository.save(user);
            return BasicResponse.builder()
                    .status( HttpStatus.OK.value())
                    .message("Success")
                    .description("changes updated successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }


    public void checkForBlockedUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.isBlocked()) {
                // Check if user is logged in (session data example)
                if (SecurityContextHolder.getContext().getAuthentication() != null
                        && SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getEmail())) {
                    // Invalidate session (replace with your session management logic)
                    SecurityContextHolder.clearContext();
                    // Optionally, send a broadcast message (e.g., using a message queue)
                    // to notify clients about the logout
                }
            }
        }
    }

}
