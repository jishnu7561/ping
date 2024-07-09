package com.ping.chat_service.controller;

import com.ping.chat_service.dto.*;
import com.ping.chat_service.feign.UserClient;
import com.ping.chat_service.model.Chat;
import com.ping.chat_service.model.Message;
import com.ping.chat_service.repository.ChatRepository;
import com.ping.chat_service.repository.MessageRepository;
import com.ping.chat_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/createChat/{receiverId}")
    public ResponseEntity<Chat> createChat(@PathVariable Integer receiverId,
                                           @RequestHeader("Authorization") String header){
        return new ResponseEntity<Chat>(chatService.createChat(header,receiverId), HttpStatus.CREATED);
    }

//    @PostMapping("/sendMessage")
//    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest messageRequest,
//                                               @RequestHeader("Authorization") String header) {
//        return new ResponseEntity<Message>(chatService.sendMessage(header,messageRequest), HttpStatus.CREATED);
//    }

    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public MessageResponse sendMessage(@Payload MessageRequest messageRequest) {
        // Handle the message and save it to the database if needed
        // Return the message response
        System.out.println("sendMessage endpoint get called");
        Message message = chatService.sendMessage(messageRequest);
        simpMessagingTemplate.convertAndSend("/group/"+message.getChat().getId().toString(),message);
        return new MessageResponse(message.getId(), message.getSender(), message.getReceiver(), message.getContent(), message.getCreatedAt(),message.getIsRead());
    }

    @GetMapping("/getAllChat/{receiverId}")
    public ResponseEntity<List<Message>> getAllChats(@PathVariable Integer receiverId,
                                            @RequestHeader("Authorization") String header){
        return new ResponseEntity<List<Message>>(chatService.getChatsByUser(header,receiverId), HttpStatus.OK);
    }

    @GetMapping("/chatting")
    public ResponseEntity<List<ChattingUserResponse>> getChattingUsers(@RequestHeader("Authorization") String header) {
        return new ResponseEntity<List<ChattingUserResponse>>(chatService.getDefaultChattingUsers(header),HttpStatus.OK);
    }

    @GetMapping("/search/{query}")
    public List<ChattingUserResponse> searchUsers(@PathVariable String query,
                                                  @RequestHeader("Authorization") String header) {
        System.out.println("searchquery: "+query);
        return chatService.searchUsers(query,header);
    }

    @GetMapping("/getMessages/{chatId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable Integer chatId,
                                                             @RequestHeader("Authorization") String header){
//        Optional<List<Message>> messageList = messageRepository.findByGroupId(chatId);
//        if(messageList.isPresent()){
//            return ResponseEntity.ok(messageList.get());
//        }
//        List<Message> messages = messageRepository.findMessagesByChatId(chatId);
        return ResponseEntity.ok(chatService.getMessages(chatId));
    }

    @GetMapping("/getUserDetails/{chatId}")
    public ResponseEntity<User> getDetails(@PathVariable Integer chatId,
                                                     @RequestHeader("Authorization") String header){
        return ResponseEntity.ok(chatService.getUserDetailsByChatId(chatId,header));
    }

}
