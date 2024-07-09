package com.ping.apigateway.filter;

import com.ping.apigateway.Exception.AuthHeaderNotFountException;
import com.ping.apigateway.Exception.TokenInvalidException;
import com.ping.apigateway.Exception.UserBlockedException;
import com.ping.apigateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>{
    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private JwtUtils jwtUtils;
    public AuthenticationFilter (){
        super(Config.class);
    }

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public GatewayFilter apply (Config config) {
        return ((exchange, chain) -> {
            log.info ( "inside gateway filter" );
            if( routeValidator.isSecured.test ( exchange.getRequest () ))
            {
                try
                {
                    if(!exchange.getRequest ().getHeaders ().containsKey ( HttpHeaders.AUTHORIZATION ))
                    {
                        throw new AuthHeaderNotFountException ( "missing authorization header" );
                    }
                } catch (AuthHeaderNotFountException e) {
                    log.error ( "Exception" + e.getMessage () );
                    throw new AuthHeaderNotFountException ( "auth header is not fount please provide token" );
                }

                String authHeader = exchange.getRequest ().getHeaders ().get ( HttpHeaders.AUTHORIZATION ).get ( 0 );
                if(authHeader != null && authHeader.startsWith( "Bearer " )) {
                    authHeader = authHeader.substring ( 7 );
                }

                try {
                    log.info ( "checking token expired or not " + authHeader );
                    jwtUtils.isTokenExpired( authHeader );


                    String userName = getUserIdFromToken(authHeader);
//                    String userServiceUrl = "http://localhost:8083/user/api/auth/status/"+userName;
//                    String status = restTemplate.getForObject(userServiceUrl, String.class);
//                    String userSubscribeUrl = "http://localhost:8083/user/api/auth/isSubscribed/"+userName;
//                    String isSubscribed = restTemplate.getForObject(userSubscribeUrl, String.class);

//                    if ("BLOCKED".equalsIgnoreCase(status)) {
//                        throw new UserBlockedException("account is blocked");
//                    }
                }
                catch ( ExpiredJwtException e ){
                    throw new TokenInvalidException("Token Expired, please login again");
                }
                catch ( UserBlockedException e ){
                    throw new UserBlockedException(e.getMessage());
                }
                catch (Exception e)
                {
                    throw new RuntimeException ( e.getMessage () );
                }
            }
            return chain.filter ( exchange );
        });

    }
    public static class Config {

    }

    private String getUserIdFromToken(String token) {
        Claims claims = jwtUtils.extractAllClaims(token);
        return claims.getSubject(); // Assuming user ID is in the subject
    }
}
