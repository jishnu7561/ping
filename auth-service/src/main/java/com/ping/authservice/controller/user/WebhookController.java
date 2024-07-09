package com.ping.authservice.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ping.authservice.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/user/api/auth/webhook")
public class WebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload, HttpServletRequest request) throws JsonProcessingException {
        System.out.println("Webhook endpoint hit");

        // Log the Stripe signature header
        String sigHeader = request.getHeader("Stripe-Signature");
        System.out.println("Stripe-Signature id: " + request.getRequestId());
        System.out.println("Stripe-Signature if: " + payload.contains("id"));

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(payload);
            String email = jsonNode.path("data").path("object").path("customer_details").path("email").asText();
            String stripeId = jsonNode.path("data").path("object").path("id").asText();
            System.out.println("Stripe-Signature id: " + stripeId);

            // Verify the signature
            Event event;

            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            System.out.println("Event verified successfully");


        // Handle different event types
            switch (event.getType()) {
                case "checkout.session.completed":
                    System.out.println("Handling checkout.session.completed");
                    paymentService.handlePaymentSuccess(email,stripeId);
                    break;

                case "payment_intent.payment_failed":
                    System.out.println("Handling payment_intent.payment_failed");

//                  paymentService.handlePaymentFailure(email);
                    break;

                default:
                    System.out.println("called the default :" + event.getType());
                    break;
            }

        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            System.out.println("Signature verification failed: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return ResponseEntity.ok("Webhook handled");
    }



//    @PostMapping
//    public ResponseEntity<Void> webhooks(@RequestBody String json, HttpServletRequest httpRequest) {
//        Event event = validateStripeHeadersAndReturnEvent(json, httpRequest.getHeader("Stripe-Signature"));
////        StripeObject stripeObject = getStripeObject(event);
////        PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
////        paymentService.handlePaymentSuccess(paymentIntent);
//        System.out.println("successsssss");
//        System.out.println("event:"+event);
//        System.out.println("event type:"+event.getType());
////        paymentService.handlePaymentFailure(event.getType(), paymentIntent);
//        return ResponseEntity.status(HttpStatus.OK).build();
//    }

//    private Event validateStripeHeadersAndReturnEvent(String payload, String headers) {
//        try {
//            return Webhook.constructEvent(
//                    payload, headers, endpointSecret
//            );
//        } catch (JsonSyntaxException e) {
////            throw new UnAuthorizedException("Invalid payload");
//            throw new RuntimeException("  ");
//        } catch (SignatureVerificationException e) {
////            throw new UnAuthorizedException("Invalid Signature");
//            System.out.println("SignatureVerificationException: "+e.getMessage());
//        }
//        return null;
//    }
}
