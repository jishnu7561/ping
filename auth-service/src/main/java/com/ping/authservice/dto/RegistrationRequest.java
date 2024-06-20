package com.ping.authservice.dto;

import lombok.Data;

@Data
public class RegistrationRequest {

    private String fullName;
    private String accountName;
    private String email;
    private String password;
}
