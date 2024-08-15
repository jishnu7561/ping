package com.ping.chat_service.controller;

import com.ping.chat_service.dto.ChattingUserResponse;
import com.ping.chat_service.dto.MessageRequest;
import com.ping.chat_service.dto.MessageResponse;
import com.ping.chat_service.model.Message;
import com.ping.chat_service.repository.ChatRepository;
import com.ping.chat_service.service.ChatService;
import com.ping.chat_service.service.NotificationService;
import com.ping.chat_service.util.BasicResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
@AllArgsConstructor
public class RealtimeChat {

    @Autowired
    private ChatService chatService;

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/sendMessage")  //  /app/sendMessage
    public MessageResponse receiveMessage(@Payload MessageRequest messageRequest) {
        System.out.println("header at chat websocket: "+messageRequest);
        MessageResponse savedMessage = chatService.sendMessage(messageRequest);
        List<ChattingUserResponse> responseList = chatService.getDefaultChattingUsers(messageRequest.getHeader());
        System.out.println("senMessage endpoint called successfully");
        simpMessagingTemplate.convertAndSend("/chat/"+messageRequest.getChatId(),savedMessage);
        simpMessagingTemplate.convertAndSend("/chatting-users",messageRequest);

        System.out.println("message : "+messageRequest.toString());
        return savedMessage;
    }

    @MessageMapping("/isRead")
    public List<MessageResponse> markMessagesAsRead(@Payload MessageRequest messageRequest) {
        System.out.println("called the markMessagesAsRead() method by chatId= "+messageRequest.getChatId());
        chatService.markMessagesAsRead(messageRequest.getHeader(), messageRequest.getChatId());
        List<MessageResponse> messageResponses = chatService.getMessages(messageRequest.getChatId());
        System.out.println("message Response: "+ messageResponses);
        simpMessagingTemplate.convertAndSend("/chats/"+messageRequest.getChatId(),messageResponses);
        return messageResponses;
    }

    @MessageMapping("/delete-message")
    public Integer deleteMessage(@Payload MessageRequest messageRequest) {
        System.out.println("dlete message:"+messageRequest.getMessageId());
        chatService.deleteMessage(messageRequest.getMessageId());
        simpMessagingTemplate.convertAndSend("/chat/message-deleted/"+messageRequest.getChatId(),messageRequest.getMessageId());
        return messageRequest.getMessageId();
    }

    @MessageMapping("/isReadNotification")
    public void markNotificationAsRead(@Payload MessageRequest messageRequest) {
        System.out.println("called the markMessagesAsRead() method by chatId= "+messageRequest.getUserId());
        notificationService.markNotificationAsRead(messageRequest.getUserId());
    }


}
