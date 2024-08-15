package com.ping.chat_service.service;

import com.ping.chat_service.dto.*;
import com.ping.chat_service.exception.ChatNotFoundException;
import com.ping.chat_service.exception.UserNotFoundException;
import com.ping.chat_service.feign.UserClient;
import com.ping.chat_service.model.Chat;
import com.ping.chat_service.model.Message;
import com.ping.chat_service.repository.ChatRepository;
import com.ping.chat_service.repository.MessageRepository;
import com.ping.chat_service.util.BasicResponse;
import com.ping.chat_service.util.TimeAgoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private TimeAgoUtil timeAgoUtil;

    @Autowired
    private MessageRepository messageRepository;

    public Chat createChat(String header,Integer user2Id){

        try {
            User user = userClient.getUser(header).getBody();
            if(user == null) {
                throw new UserNotFoundException("user not found");
            }
            // Check if a chat between these users already exists
            Optional<Chat> existingChat = chatRepository.findByUser1IdAndUser2Id(user.getId(), user2Id)
                    .or(() -> chatRepository.findByUser1IdAndUser2Id(user2Id, user.getId()));

            if (existingChat.isPresent()) {
                return existingChat.get();
            }

            Chat newChat = Chat.builder()
                    .user1Id(user.getId())
                    .user2Id(user2Id)
                    .lastMessage(null)
                    .lastMessageDate(null)
                    .build();

            chatRepository.save(newChat);

            return newChat;
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public Message sendMessage(String header, MessageRequest messageRequest) {
        try {
            User user = userClient.getUser(header).getBody();
            if(user == null) {
                throw new UserNotFoundException("user not found");
            }

            Chat chat = chatRepository.findById(messageRequest.getChatId()).orElseThrow(() -> new ChatNotFoundException("chat not found"));

            Message message = Message.builder()
                    .sender(user.getId())
                    .receiver(messageRequest.getReceiverId())
                    .content(messageRequest.getContent())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .chat(chat)
                    .build();
            messageRepository.save(message);

            chat.setLastMessage(messageRequest.getContent());
            chat.setLastMessageDate(LocalDateTime.now());
            chatRepository.save(chat);

            return message;

        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (ChatNotFoundException ex){
            throw new ChatNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    @Transactional
    public MessageResponse sendMessage(MessageRequest messageRequest) {
        try {
//            User user = userClient.getUser(header).getBody();
//            if(user == null) {
//                throw new UserNotFoundException("user not found");
//            }

            Chat chat = chatRepository.findById(messageRequest.getChatId()).orElseThrow(() -> new ChatNotFoundException("chat not found"));

            Message message = Message.builder()
                    .sender(messageRequest.getSenderId())
                    .receiver(messageRequest.getReceiverId())
                    .content(messageRequest.getContent())
                    .createdAt(LocalDateTime.now())
                    .isRead(false)
                    .chat(chat)
                    .build();
            Message savedMessage = messageRepository.save(message);

            chat.setLastMessage(messageRequest.getContent());
            chat.setLastMessageDate(LocalDateTime.now());
            chatRepository.save(chat);

            MessageResponse messageResponse = MessageResponse.builder()
                    .id(savedMessage.getId())
                    .sender(savedMessage.getSender())
                    .receiver(savedMessage.getReceiver())
                    .content(savedMessage.getContent())
                    .createdAt(timeAgoUtil.formatDateTime(savedMessage.getCreatedAt()))
                    .isRead(savedMessage.getIsRead())
                    .build();

            System.out.println("saved message: "+savedMessage.getId());
            return messageResponse;

        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (ChatNotFoundException ex){
            throw new ChatNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<Message> getChatsByUser(String header, Integer chatId) {

        try{
            User user = userClient.getUser(header).getBody();
            if(user == null) {
                throw new UserNotFoundException("user not found");
            }
            Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatNotFoundException("chat not found"));
            return chat.getMessages();
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (ChatNotFoundException ex){
            throw new ChatNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<ChattingUserResponse> getDefaultChattingUsers(String header) {
        try{
            User user = userClient.getUser(header).getBody();
            if(user == null) {
                throw new UserNotFoundException("user not found");
            }
            List<Chat> chats = chatRepository.findByUser1IdOrUser2Id(user.getId(), user.getId());
            List<Integer> userIdList = chats.stream()
                .map(chat -> chat.getUser1Id().equals(user.getId()) ? chat.getUser2Id() : chat.getUser1Id())
                .distinct()
                .toList();


            List<ChattingUserResponse> responseList = new ArrayList<>();

            for(Chat chat : chats) {
                User user1 = userClient.getUserIfExist(getUserId(chat,user.getId())).getBody();
                ChattingUserResponse details = ChattingUserResponse.builder()
                        .chatId(chat.getId())
                        .lastMessage(chat.getLastMessage())
                        .lastMessageDate(formatLastMessageDate(chat.getLastMessageDate()))
                        .accountName(user1.getAccountName())
                        .imageUrl(user1.getImageUrl())
                        .userId(user1.getId())
                        .unreadMessage(getUnreadMessagesCount(user.getId(),chat.getId()))
                        .build();
                responseList.add(details);
            }
         return responseList;
        }catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (ChatNotFoundException ex){
            throw new ChatNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public Integer getUserId(Chat chat,Integer id) {
        if(Objects.equals(chat.getUser1Id(), id)) {
            return chat.getUser2Id();
        } else{
            return chat.getUser1Id();
        }
    }

    public List<ChattingUserResponse> searchUsers(String query,String header) {
        try{

            User user1 = userClient.getUser(header).getBody();
            if(user1 == null) {
                throw new UserNotFoundException("user not found");
            }
            List<ChattingUserResponse> response = new ArrayList<>();
//            if(Objects.equals(query, "")) {
                List<User> userList = userClient.getUserByUserName(query).getBody();
                for (User user2 : userList) {
                    ChattingUserResponse chattingUserResponse = new ChattingUserResponse();
                    System.out.println(user2.getAccountName());
                    chattingUserResponse.setUserId(user2.getId());
                    chattingUserResponse.setAccountName(user2.getAccountName());
                    chattingUserResponse.setImageUrl(user2.getImageUrl());
                    Optional<Chat> chat = chatRepository.findByUser1IdAndUser2Id(user1.getId(), user2.getId());
                    if (chat.isEmpty()) {
                        chat = chatRepository.findByUser1IdAndUser2Id(user2.getId(), user1.getId());
                    }
                    if (chat.isPresent()) {
                        chattingUserResponse.setChatId(chat.get().getId());
                        chattingUserResponse.setLastMessage(chat.get().getLastMessage());
                        chattingUserResponse.setLastMessageDate(formatLastMessageDate(chat.get().getLastMessageDate()));
                        chattingUserResponse.setUnreadMessage(getUnreadMessagesCount(user1.getId(),chat.get().getId()));
                    }
                    response.add(chattingUserResponse);
                }
                return response;
//            }
//            return getDefaultChattingUsers(header);

        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

//    public List<User> searchUsers(String query) {
//        return userRepository.findByUsernameContainingIgnoreCase(query);
//    }

    public String formatLastMessageDate(LocalDateTime lastMessageDate) {
        if (lastMessageDate == null) {
            return ""; // or handle it as per your application's requirements
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        LocalDate messageDate = lastMessageDate.toLocalDate();

        if (messageDate.equals(today)) {
            // Show time if the message was sent today
            return lastMessageDate.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (messageDate.equals(yesterday)) {
            // Show "Yesterday" if the message was sent yesterday
            return "Yesterday";
        } else {
            // Show the date in the format "MMM d"
            return lastMessageDate.format(DateTimeFormatter.ofPattern("MMM d"));
        }
    }

    public User getUserDetailsByChatId(Integer chatId,String header) {
        try {
            User user1 = userClient.getUser(header).getBody();
            if(user1 == null) {
                throw new UserNotFoundException("user not found");
            }
            Optional<Chat>  chat= chatRepository.findById(chatId);
            if(chat.isEmpty()){
                return null;
            }

            Integer userId = getUserId(chat.get(),user1.getId());

            User user = userClient.getUserIfExist(userId).getBody();

            return User.builder()
                    .accountName(user.getAccountName())
                    .imageUrl(user.getImageUrl())
                    .id(user.getId())
                    .build();

        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<MessageResponse> getMessages(Integer chatId) {

        List<Message> messageList = messageRepository.findMessagesByChatId(chatId);
        List<MessageResponse> messageResponseList = new ArrayList<>();
        for (Message message : messageList) {
            System.out.println("message: "+message.getContent());
            messageResponseList.add(MessageResponse.builder()
                    .id(message.getId())
                    .sender(message.getSender())
                    .receiver(message.getReceiver())
                    .content(message.getContent())
                    .createdAt(timeAgoUtil.formatDateTime(message.getCreatedAt()))
                    .isRead(message.getIsRead())
                    .build());
        }
        return messageResponseList;
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public Integer getUnreadMessagesCount(Integer userId, Integer chatId) {
        try{
//            User user = userClient.getUser(header).getBody();
            return messageRepository.countUnreadMessages(userId,chatId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public BasicResponse markMessagesAsRead(String header, Integer chatId) {
        try{
            User user = userClient.getUser(header).getBody();
            messageRepository.markMessagesAsRead(user.getId(),chatId);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("successful")
                    .description("Messages get marked as read successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (Exception e){
            throw new  RuntimeException(e.getMessage());
        }
    }

    public BasicResponse deleteMessage(Integer messageId) {
        try {
            messageRepository.deleteById(messageId);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("successful")
                    .description("message deleted successfully")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


//    @Transactional
//    public void markMessagesAsRead(Integer receiverId, Integer chatId) {
//        messageRepository.markMessagesAsRead(receiverId, chatId);
//    }

//    public Integer getUnreadMessagesCount(Integer receiverId, Integer chatId) {
//        return messageRepository.countUnreadMessages(receiverId, chatId);
//    }
}
