package com.ping.apigateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    public static final List<String> openApiEndpoints = List.of(
            "/user/api/auth/register",
            "/user/api/auth/authenticate",
            "/user/api/auth/otpVerification",
            "/user/api/auth/webhook",
            "/user/api/auth/resend-otp",
            "/user/api/auth/status/**",
            "/user/api/auth/google",
            "/user/api/auth/**",
            "user/api/auth/getAllUsers",
            "/eureka/**"
    );

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    public Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> pathMatcher.match(uri, request.getURI().getPath()));

}
