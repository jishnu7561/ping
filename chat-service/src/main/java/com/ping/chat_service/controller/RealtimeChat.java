package com.ping.chat_service.controller;

import com.ping.chat_service.model.Message;
import com.ping.chat_service.repository.ChatRepository;
import com.ping.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class RealtimeChat {

    @Autowired
    private ChatService chatService;

    private SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/message")
//    @SendTo("/group/public")
    public  Message receiveMessage(@Payload Message message) {
        Message savedMessage = chatService.saveMessage(message);
        simpMessagingTemplate.convertAndSend("/group/"+message.getChat().getId().toString(),savedMessage);
        System.out.println("called the websocket ");
        return message;
    }
}
