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


//    @Query("SELECT DATE_FORMAT(u.createdAt, '%Y-%m-%d') as date, COUNT(u) as count " +
//            "FROM User u " +
//            "WHERE u.createdAt >= :startDate " +
//            "GROUP BY DATE_FORMAT(u.createdAt, '%Y-%m-%d') " +
//            "ORDER BY date")
//    List<Object[]> countUsersByDay(@Param("startDate") LocalDateTime startDate);
//
//    @Query("SELECT DATE_FORMAT(u.createdAt, '%Y-%m') as date, COUNT(u) as count " +
//            "FROM User u " +
//            "WHERE u.createdAt >= :startDate " +
//            "GROUP BY DATE_FORMAT(u.createdAt, '%Y-%m') " +
//            "ORDER BY date")
//    List<Object[]> countUsersByMonth(@Param("startDate") LocalDateTime startDate);
//
//    @Query("SELECT DATE_FORMAT(u.createdAt, '%Y') as date, COUNT(u) as count " +
//            "FROM User u " +
//            "WHERE u.createdAt >= :startDate " +
//            "GROUP BY DATE_FORMAT(u.createdAt, '%Y') " +
//            "ORDER BY date")
//    List<Object[]> countUsersByYear(@Param("startDate") LocalDateTime startDate);

    List<User> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Long countByIsBlocked(boolean b);

    Page<User> findByAccountNameContainingIgnoreCase(String search,Pageable pageable);
}
