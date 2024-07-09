package com.ping.chat_service.repository;

import com.ping.chat_service.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Integer> {

    Optional<Chat> findByUser1IdAndUser2Id(Integer user1Id, Integer user2Id);

    List<Chat> findByUser1IdOrUser2Id(Integer userId, Integer userId1);
}
