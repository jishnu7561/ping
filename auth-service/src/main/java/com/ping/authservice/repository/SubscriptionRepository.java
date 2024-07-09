package com.ping.authservice.repository;

import com.ping.authservice.model.Subscription;
import com.ping.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription,Integer> {


    Subscription findByStripeSubscriptionId(String stripeId);
}
