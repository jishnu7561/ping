package com.ping.postservice.controller;

//import com.ping.common.dto.Customer;
import com.ping.postservice.kafka.KafkaMessagePublisher;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PublisherController {

    @Autowired
    private KafkaMessagePublisher messagePublisher;

//    @GetMapping("/publish-message/{message}")
//    public ResponseEntity<?> publishMessage(@PathVariable String message) {
//        try{
//            for (int i=0;i<=1000;i++) {
//                messagePublisher.sendMessage("notification",message);
//            }
//            return ResponseEntity.ok("Message published successfully ..");
//
//        } catch (Exception ex) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .build();
//        }
//    }

//    @PostMapping("/publish-message")
//    public void publishEvent(@RequestBody Customer customer) {
//        messagePublisher.sendEvent("ping-demo",customer);
//    }
}
