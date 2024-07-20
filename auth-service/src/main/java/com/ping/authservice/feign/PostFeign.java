package com.ping.authservice.feign;

import com.ping.authservice.dto.ReportPostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("POST-SERVICE")
public interface PostFeign {

    @GetMapping("/post/getPostCount")
    public ResponseEntity<Long> getPostCount();

    @GetMapping("/post/getReports")
    public List<ReportPostResponse> getAllReports();
}
