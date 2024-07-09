package com.ping.authservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.model.Subscription;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.SubscriptionRepository;
import com.ping.authservice.repository.UserRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class PaymentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Value("${stripe.secret.key}")
    private String STRIPE_SECRET_KEY ;

    public String createPaymentLink(String header) {
        Stripe.apiKey = STRIPE_SECRET_KEY;
        try {

            User user = userService.findUserByHeader(header);

            SessionCreateParams params = SessionCreateParams.builder()
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:3000/profile")
                    .setCancelUrl("http://localhost:3000/fail")
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("USD")
                                    .setUnitAmount((long) 600 * 100)
                                    .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                            .setName("ping")
                                            .build())
                                    .build())
                            .build())
                    .setCustomerEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                    .build();

            Session session = Session.create(params);
            System.out.println("session :" + session.getId());
            saveSubscriptionDetails(session,user);
            return session.getUrl();

        }catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void saveSubscriptionDetails(Session session,User user) {
        Subscription subscription = new Subscription();
        subscription.setStripeSubscriptionId(session.getId());
        subscription.setUser(user);
        subscription.setStatus("PENDING");
        subscriptionRepository.save(subscription);
        System.out.println("Created subscription with user details successfully");
    }


    public void handlePaymentSuccess(String email,String stripeId) {

        try{

            Subscription subscription = subscriptionRepository.findByStripeSubscriptionId(stripeId);
            if(subscription == null && subscription.getUser() == null){
                throw new UsernameNotFoundException("user not found");
            }

            subscription.setSubscriptionStartDate(LocalDateTime.now());
            subscription.setSubscriptionEndDate(LocalDateTime.now().plusMonths(1).truncatedTo(ChronoUnit.SECONDS));
            subscription.setStatus("ACTIVE");
            subscriptionRepository.save(subscription);

            User user = subscription.getUser();
            user.setSubscriptionEndDate(subscription.getSubscriptionEndDate());
            user.setSubscriptionId(subscription.getId());
            user.setSubscribed(true);
            userRepository.save(user);

        }catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("user not found");
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        System.out.println("Subscription for blue tick successfully done , only for one month");
    }

}
