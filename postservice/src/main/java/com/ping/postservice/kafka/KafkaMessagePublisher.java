package com.ping.postservice.kafka;

import com.ping.common.dto.Customer;
import com.ping.common.dto.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaMessagePublisher {

    @Autowired
    private KafkaTemplate<String,Object> template;

    public void sendMessage(String topic , String  message) {
        CompletableFuture<SendResult<String, Object>> future = template.send(topic, message);
        future.whenComplete((result,ex)-> {
            if (ex == null) {
                System.out.println("Send message=[" + message +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");

            } else {
                System.out.println("Unable to send message=[" + message +
                        "] due to : " + ex.getMessage());

            }
        });
    }

    public void sendNotification(String topic , Notification notification) {
        try {
            CompletableFuture<SendResult<String, Object>> future = template.send(topic, notification);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    System.out.println("Send message=[" + notification.toString() +
                            "] with offset=[" + result.getRecordMetadata().offset() + "]");

                } else {
                    System.out.println("Unable to send message=[" + notification.toString() +
                            "] due to : " + ex.getMessage());

                }
            });
        } catch (Exception ex) {
            System.out.println("ERROR :  "+ ex.getMessage());
        }
    }
}
