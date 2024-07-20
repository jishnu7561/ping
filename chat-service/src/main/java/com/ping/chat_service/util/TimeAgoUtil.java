package com.ping.chat_service.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeAgoUtil {

    public String timeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);

        long seconds = duration.getSeconds();
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;
        long months = days / 30; // Approximation
        long years = days / 365; // Approximation

        if (seconds < 60) {
            return seconds + " sec" + (seconds > 1 ? "s" : "") + " ago";
        } else if (minutes < 60) {
            return minutes + " min" + (minutes > 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hr" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (weeks < 4) {
            return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
        } else if (months < 12) {
            return months + " month" + (months > 1 ? "s" : "") + " ago";
        } else {
            return years + " year" + (years > 1 ? "s" : "") + " ago";
        }
    }
}