package com.ping.authservice.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {

    public String generateOtp() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999);
        String otp = Integer.toString(randomNumber);

        while (otp.length() < 6) {
            otp= "0" + otp;
        }

        return otp;
    }
}
