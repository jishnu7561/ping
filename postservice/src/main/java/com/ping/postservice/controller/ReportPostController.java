package com.ping.postservice.controller;

import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.ReportPostRequest;
import com.ping.postservice.dto.ReportPostResponse;
import com.ping.postservice.model.ReportPost;
import com.ping.postservice.repository.ReportPostRepository;
import com.ping.postservice.service.ReportPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class ReportPostController {

    @Autowired
    private ReportPostService reportPostService;

    @Autowired
    private ReportPostRepository reportPostRepository;

    @PostMapping("/reportPost")
    public ResponseEntity<BasicResponse> reportPost(@RequestBody ReportPostRequest request) {
        System.out.println(request);
        return ResponseEntity.ok(reportPostService.saveReportPost(request));

//        return ResponseEntity.ok(BasicResponse.builder().build());
    }

    @GetMapping("/getReports")
    public List<ReportPostResponse> getAllReports() {
        return reportPostService.getAllPost();
    }
}
