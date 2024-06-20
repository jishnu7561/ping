package com.ping.postservice.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    private Integer id;

    private String fullName;
    private String accountName;
    private String email;
    private String password;
    private String bio;
    private boolean isBlocked;
    private String imageUrl;

    private String role;
}
