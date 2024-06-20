package com.ping.authservice.controller.admin;

import com.ping.authservice.model.User;
import com.ping.authservice.service.AdminService;
import com.ping.authservice.service.UserService;
import com.ping.authservice.util.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/getAllUsers")
    public List<User> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/block-user/{id}")
    public ResponseEntity<BasicResponse> manageBlockAndUnBlock(@PathVariable("id") int id) {
        return ResponseEntity.ok(adminService.manageBlockAndUnBlock(id));
    }

}
