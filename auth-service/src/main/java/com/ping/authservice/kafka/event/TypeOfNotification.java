package com.ping.authservice.kafka.event;

public enum TypeOfNotification {

    LIKE,
    COMMENT,

    FOLLOW,
    UNFOLLOW,
    FRIEND_REQUEST,
    FRIEND_REQUEST_ACCEPTED
}