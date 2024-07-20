package com.ping.postservice.feign;

import com.ping.postservice.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient("AUTH-SERVICE")
public interface UserClient {

    @GetMapping("/user/api/auth/isUserExist/{id}")
    public ResponseEntity<User> getUserIfExist(@PathVariable Integer id);

    @GetMapping("/user/api/auth/getUser")
    public ResponseEntity<User> getUser(@RequestHeader("Authorization") String header);


}
