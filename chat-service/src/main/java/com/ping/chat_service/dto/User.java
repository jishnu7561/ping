package com.ping.chat_service.dto;

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
    private boolean isPrivate;
    private boolean isSubscribed;

    private String role;
}