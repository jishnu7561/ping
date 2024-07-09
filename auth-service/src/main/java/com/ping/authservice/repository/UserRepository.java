package com.ping.authservice.repository;

import com.ping.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);


    Optional<User> findByAccountName(String userName);

    List<User> findByIdIn(List<Integer> userIds);

    List<User> findByAccountNameContainingIgnoreCase(String userName);
}
