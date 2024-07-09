package com.ping.authservice.controller.user;

import com.ping.authservice.service.PaymentService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/api/secure/subscription")
public class SubscriptionController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-subscription-intent")
    public ResponseEntity<String> createSubscriptionIntent(@RequestHeader("Authorization") String header) {
        return ResponseEntity.ok(paymentService.createPaymentLink(header));
    }

}
