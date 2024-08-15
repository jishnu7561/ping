package com.ping.authservice.repository;

import com.ping.authservice.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByEmail(String email);


    Optional<User> findByAccountName(String userName);

    List<User> findByIdIn(List<Integer> userIds);

    List<User> findByAccountNameContainingIgnoreCase(String userName);


    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<User> findByLastLoginAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    Long countByIsBlocked(boolean b);

    Page<User> findByAccountNameContainingIgnoreCase(String search,Pageable pageable);
}
