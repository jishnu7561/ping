package com.ping.chat_service.feign;

import com.ping.chat_service.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("AUTH-SERVICE")
public interface UserClient {

    @GetMapping("/user/api/auth/isUserExist/{id}")
    public ResponseEntity<User> getUserIfExist(@PathVariable Integer id);

    @GetMapping("/user/api/auth/getUser")
    public ResponseEntity<User> getUser(@RequestHeader("Authorization") String header);

    @PostMapping("/user/api/auth/getAllUsers")
    public ResponseEntity<List<User>> getAllUsersById(@RequestBody List<Integer> userIds);

    @GetMapping("/user/api/auth/findByUsername/{userName}")
    public ResponseEntity<List<User>> getUserByUserName(@PathVariable String userName);

}