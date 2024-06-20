package com.ping.authservice.dto;

import com.ping.authservice.model.Role;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDetailsResponse {
    private long userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private boolean isEmailVerified;
    private String profile_image_path;
    private boolean isBlocked;
    private Date joinDate;
    private String errorMessage;
}