package com.ping.chat_service.feign;

import com.ping.chat_service.dto.PostResponse;
import com.ping.chat_service.dto.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("POST-SERVICE")
public interface PostFeign {

    @GetMapping("/post/post-details/{id}")
    public ResponseEntity<PostResponse> getPostDetails(@PathVariable Integer id);
}
