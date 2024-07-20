package com.ping.chat_service.repository;

import com.ping.chat_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Integer> {

    List<Notification> findByReceiver(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.receiver = :receiverId")
    void markNotificationAsRead(@Param("receiverId") Integer receiverId);

    @Query("SELECT COUNT(*) FROM Notification n WHERE (n.isRead = false OR n.isRead IS NULL) AND n.receiver = :receiverId ")
    Integer countUnreadNotification(@Param("receiverId") Integer receiverId);


}
