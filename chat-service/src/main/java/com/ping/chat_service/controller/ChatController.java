package com.ping.chat_service.controller;

import com.ping.chat_service.dto.*;
import com.ping.chat_service.feign.UserClient;
import com.ping.chat_service.model.Chat;
import com.ping.chat_service.model.Message;
import com.ping.chat_service.repository.ChatRepository;
import com.ping.chat_service.repository.MessageRepository;
import com.ping.chat_service.service.ChatService;
import com.ping.chat_service.service.NotificationService;
import com.ping.chat_service.util.BasicResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private NotificationService notificationService;

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

    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/send")
    @SendTo("/topic/messages/{chatId}") // Broadcast to subscribed clients for the chat
    public ResponseEntity<String> sendMessage(@Payload MessageRequest messageRequest,
                                                       @RequestHeader("Authorization") String header) {
        System.out.println("message endpoint called");
//        try {
//            Message message = chatService.sendMessage(header, messageRequest);
//            messagingTemplate.convertAndSendToUser(
//                    messageRequest.getChatId().toString(),
//                    "/topic/messages/" + message.getChat().getId(),
//                    message);
//            return ResponseEntity.ok(MessageResponse.builder()
//                            .content(message.getContent())
//                            .id(message.getChat().getId())
//                    .build()); // Convert to MessageResponse
//        } catch (Exception e) {
//            // Handle exceptions (e.g., validation errors, authorization failures)
//            // Log the error and return an appropriate error response
//            System.err.println("Error sending message: " + e.getMessage());
//            throw new RuntimeException(e.getMessage());
        return ResponseEntity.ok("message");
//        }
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

    @GetMapping("/search/{search}")
    public List<ChattingUserResponse> searchUsers(@PathVariable String search,
                                                  @RequestHeader("Authorization") String header) {
        System.out.println("searchquery  is  : "+search);
        return chatService.searchUsers(search,header);
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

//    @PutMapping("/isRead/{chatId}")
//    public ResponseEntity<BasicResponse> markMessagesAsRead(@RequestHeader("Authorization") String header,
//                                                            @PathVariable Integer chatId) {
//        System.out.println("called the markMessagesAsRead() method by chatId= "+chatId);
//        return ResponseEntity.ok(chatService.markMessagesAsRead(header, chatId));
//    }

//    @GetMapping("/unreadCount/{chatId}")
//    public ResponseEntity<Integer> getUnreadMessagesCount(@PathVariable Integer chatId,
//                                                          @RequestHeader("Authorization") String header) {
//        Integer unreadCount = chatService.getUnreadMessagesCount(header, chatId);
//        return ResponseEntity.ok(unreadCount);
//    }

    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<BasicResponse> deleteMessage(@PathVariable Integer messageId){
        return ResponseEntity.ok(chatService.deleteMessage(messageId));
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@PathVariable Integer userId) {
        return ResponseEntity.ok(notificationService.getAllNotifications(userId));
    }

    @DeleteMapping("/delete-notification/{notificationId}")
    public ResponseEntity<BasicResponse> deleteNotification(@PathVariable Integer notificationId){
        return ResponseEntity.ok(notificationService.deleteNotification(notificationId));
    }

    @GetMapping("/notificationCount/{id}")
    public Integer notificationCount(@PathVariable Integer id){
        return notificationService.getNotificationCount(id);
    }

}
