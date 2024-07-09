package com.ping.authservice.util;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private static final String CLIENT_ID = "184243567332-nlp89m0i8r379ho082v8gk9kvkaq3prl.apps.googleusercontent.com";
//    @Value("${google.client.id}")
//    private String CLIENT_ID;

    public GoogleIdToken.Payload validateToken(String idToken) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        try {
            GoogleIdToken idTokenObject = verifier.verify(idToken);
            if (idTokenObject != null) {
                return idTokenObject.getPayload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
