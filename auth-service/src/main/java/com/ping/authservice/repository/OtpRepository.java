package com.ping.authservice.repository;

import com.ping.authservice.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Integer> {

//    Optional<Otp> findByEmail(String email);

    Otp findByEmail(String  email);

    Otp findByOtpGenerated(String token);
}
