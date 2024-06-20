package com.ping.authservice.dto;

import com.ping.authservice.model.Follow;
import com.ping.authservice.model.User;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProfileResponse {

    private String fullName;
    private String AccountName;
    private String email;
    private String bio;
    private String image;
    private Integer postCount;
    private List<Follow> followers;
    private List<Follow> following;
}
