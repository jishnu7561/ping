package com.ping.chat_service.repository;

import com.ping.chat_service.model.Chat;
import com.ping.chat_service.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message,Integer> {
//    List<Message> findMessagesByChatId(Integer chatId);
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    List<Message> findMessagesByChatId(@Param("chatId") Integer chatId);
}
