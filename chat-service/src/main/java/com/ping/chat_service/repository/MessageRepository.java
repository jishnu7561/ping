package com.ping.chat_service.repository;

import com.ping.chat_service.model.Chat;
import com.ping.chat_service.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
//    List<Message> findMessagesByChatId(Integer chatId);
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    List<Message> findMessagesByChatId(@Param("chatId") Integer chatId);

//    @Modifying
//    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiverId = :receiverId AND m.chatId = :chatId")
//    void markMessagesAsRead(@Param("receiverId") Integer receiverId, @Param("chatId") Integer chatId);

//    @Modifying
//    @Query("UPDATE Message m SET m.isRead = TRUE WHERE m.sender = :senderId AND m.receiver = :receiverId")
//    void markMessagesAsRead(Integer senderId, Integer receiverId);

    @Query("SELECT COUNT(*) FROM Message m WHERE m.isRead = false AND m.receiver = :receiverId AND m.chat.id = :chatId")
    Integer countUnreadMessages(@Param("receiverId") Integer receiverId,@Param("chatId") Integer chatId);


    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.receiver = :receiverId AND m.chat.id = :chatId")
    void markMessagesAsRead(@Param("receiverId") Integer receiverId,@Param("chatId") Integer chatId);


//    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :receiverId AND m.chatId = :chatId AND m.readStatus = false")
//    Integer countUnreadMessages(@Param("receiverId") Integer receiverId, @Param("chatId") Integer chatId);
}
