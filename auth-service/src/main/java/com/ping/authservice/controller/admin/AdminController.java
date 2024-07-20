package com.ping.authservice.controller.admin;

import com.ping.authservice.dto.ReportPostResponse;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.service.AdminService;
import com.ping.authservice.service.UserService;
import com.ping.authservice.util.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/block-user/{id}")
    public ResponseEntity<BasicResponse> manageBlockAndUnBlock(@PathVariable("id") int id) {
        return ResponseEntity.ok(adminService.manageBlockAndUnBlock(id));
    }

    @GetMapping("/getAllUsersOnSearch")
    public Page<User> getAllUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        return adminService.getAllUsersBasedOnSearch(search, PageRequest.of(page, size));
    }


    @GetMapping("/chartDetails/{period}")
    public Map<String, String> getChartDetails(@PathVariable String period) {
        Map<String, String> chartDetails = adminService.getSalesDataForLastNPeriods(period);
        return chartDetails;
    }

    @GetMapping("/reports")
    public Map<String,Long> getReports(){
        Map<String, Long> reports = adminService.getReports();
        return reports;
    }

    @GetMapping("/getReports")
    public List<ReportPostResponse> getAllReports(){
        return adminService.getAllPost();
    }
}
