package com.ping.authservice.dto;

import com.ping.authservice.model.User;
import com.ping.authservice.util.BasicResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String jwtToken;
    private User user;
    private BasicResponse basicResponse;
}
