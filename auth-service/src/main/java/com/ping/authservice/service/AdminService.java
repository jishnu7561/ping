package com.ping.authservice.service;

import com.ping.authservice.GlobalExceptionHandler.Exceptions.UsernameNotFoundException;
import com.ping.authservice.dto.ReportPostResponse;
import com.ping.authservice.feign.PostFeign;
import com.ping.authservice.model.User;
import com.ping.authservice.repository.UserRepository;
import com.ping.authservice.util.BasicResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostFeign postFeign;

    public List<User> getAllUsers() {
        List<User> listOfUsers = userRepository.findAll();
        return listOfUsers;
    }



    public BasicResponse manageBlockAndUnBlock(int userId) {
        try{
            checkForBlockedUsers();
            User user = userRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("user not found"));
            if(user.isBlocked()){
                user.setBlocked(false);
            } else {
                user.setBlocked(true);
            }
            userRepository.save(user);
            return BasicResponse.builder()
                    .status( HttpStatus.OK.value())
                    .message("Success")
                    .description("changes updated successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        }
    }


    public void checkForBlockedUsers() {
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.isBlocked()) {
                // Check if user is logged in (session data example)
                if (SecurityContextHolder.getContext().getAuthentication() != null
                        && SecurityContextHolder.getContext().getAuthentication().getName().equals(user.getEmail())) {
                    // Invalidate session (replace with your session management logic)
                    SecurityContextHolder.clearContext();
                    // Optionally, send a broadcast message (e.g., using a message queue)
                    // to notify clients about the logout
                }
            }
        }
    }

    public Map<String, String> getSalesDataForLastNPeriods(String period) {

        LocalDate currentDate = LocalDate.now();
        Map<String, String> chartDetails = new TreeMap<>();

        for (int i = 0; i < 7; i++) {
            String formattedPeriod = formatPeriod(currentDate, period);
            Integer noOfUsers = calculateSalesForPeriod(currentDate, period);

            chartDetails.put("data"+i,formattedPeriod);
            chartDetails.put("dataValue"+i, String.valueOf(noOfUsers));

            // Move to the previous period
            currentDate = moveBackOnePeriod(currentDate, period);
        }

        return chartDetails;
    }

    private String formatPeriod(LocalDate currentDate, String period) {
        DateTimeFormatter formatter;

        if ("day".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return currentDate.format(formatter);
        } else if ("month".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            return currentDate.format(formatter);
        } else if ("year".equals(period)) {
            formatter = DateTimeFormatter.ofPattern("yyyy");
            return currentDate.format(formatter);
        } else {
            throw new IllegalArgumentException("Invalid period");
        }
    }

    private Integer calculateSalesForPeriod(LocalDate currentDate, String period) {
        LocalDateTime startDateTime, endDateTime;

        if ("day".equalsIgnoreCase(period)) {
            startDateTime = currentDate.atStartOfDay();
            endDateTime = currentDate.atTime(LocalTime.MAX);
        } else if ("month".equalsIgnoreCase(period)) {
            startDateTime = currentDate.withDayOfMonth(1).atStartOfDay();
            endDateTime = currentDate.withDayOfMonth(currentDate.lengthOfMonth()).atTime(LocalTime.MAX);
        } else if ("year".equalsIgnoreCase(period)) {
            startDateTime = currentDate.withDayOfYear(1).atStartOfDay();
            endDateTime = currentDate.withDayOfYear(currentDate.lengthOfYear()).atTime(LocalTime.MAX);
        } else {
            throw new IllegalArgumentException("Invalid period");
        }

        List<User> userList = userRepository.findByCreatedAtBetween(startDateTime, endDateTime);

//        double totalSales = 0;
//
//        for (PurchaseOrder purchaseOrder : orders) {
//            for (OrderItem orderItem : purchaseOrder.getOrderItems()) {
//                if (orderItem.getOrderStatus().equals("delivered")) {
//                    System.out.println(orderItem.getOrderItemId());
//                    totalSales++;
//                }
//            }
//        }

        return userList.size();
    }

    private LocalDate moveBackOnePeriod(LocalDate currentDate, String period) {
        switch (period) {
            case "day":
                return currentDate.minusDays(1);
            case "month":
                return currentDate.minusMonths(1);
            case "year":
                return currentDate.minusYears(1);
            default:
                throw new IllegalArgumentException("Invalid period");
        }
    }

    public Map<String, Long> getReports() {
        Long totalUsers = userRepository.count();
        Long totalPosts = postFeign.getPostCount().getBody();
        Long blockedUsers = userRepository.countByIsBlocked(true);
        Map<String,Long> map = new HashMap<>();
        map.put("totalUsers",totalUsers);
        map.put("totalPosts",totalPosts);
        map.put("blockedUsers",blockedUsers);
        return map;
    }

    public Page<User> getAllUsersBasedOnSearch(String search, Pageable pageable) {
        if(Objects.equals(search, "")) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByAccountNameContainingIgnoreCase(search,pageable);
    }

    public List<ReportPostResponse> getAllPost() {
        return postFeign.getAllReports();
    }
}
