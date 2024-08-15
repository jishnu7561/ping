package com.ping.chat_service.util;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class TimeAgoUtil {

    public String timeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        System.out.println("Current time: " + now);
        System.out.println("Notification time: " + dateTime);

        Duration duration = Duration.between(dateTime, now);
        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        System.out.println("Duration in seconds: " + seconds);
        System.out.println("Duration in minutes: " + minutes);
        System.out.println("Duration in hours: " + hours);
        System.out.println("Duration in days: " + days);

        if (seconds < 60) {
            return seconds + " sec" + (seconds != 1 ? "s" : "");
        } else if (minutes < 60) {
            return minutes + " min" + (minutes != 1 ? "s" : "");
        } else if (hours < 24) {
            return hours + " hr" + (hours != 1 ? "s" : "");
        } else if (days < 7) {
            return days + " day" + (days != 1 ? "s" : "");
        } else if (days < 30) {
            long weeks = days / 7;
            return weeks + " week" + (weeks != 1 ? "s" : "");
        } else if (days < 365) {
            long months = days / 30; // Approximation
            return months + " month" + (months != 1 ? "s" : "");
        } else {
            long years = days / 365; // Approximation
            return years + " year" + (years != 1 ? "s" : "");
        }
    }

    public String formatDateTime(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();

        if (dateTime.toLocalDate().equals(now.toLocalDate())) {
            // Format time if it's today
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");
            return dateTime.format(timeFormatter);
        } else {
            // Format date and time if it's not today
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yy h:mm a");
            return dateTime.format(dateFormatter);
        }
    }
}