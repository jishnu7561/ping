package com.ping.postservice.service;

//import com.ping.common.dto.Notification;
//import com.ping.common.dto.TypeOfNotification;
import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.kafka.KafkaMessagePublisher;
import com.ping.postservice.kafka.event.Notification;
import com.ping.postservice.kafka.event.TypeOfNotification;
import com.ping.postservice.model.Like;
import com.ping.postservice.model.Post;
import com.ping.postservice.repository.LikeRepository;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.repository.SavePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SavePostRepository savePostRepository;

    @Autowired
    private KafkaMessagePublisher kafkaMessagePublisher;


    public void likePost(Integer postId, Integer userId) {
        try {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent() && !isPostLikedByUser(userId, postId)) {
                Like like = new Like();
                like.setUserId(userId);
                like.setPost(postOptional.get());
                like.setCreatedAt(LocalDateTime.now());
                Like like1 = likeRepository.save(like);

                kafkaMessagePublisher.sendNotification("notification", Notification.builder()
                        .sender(userId)
                        .receiver(postOptional.get().getUserId())
                        .typeOfNotification(TypeOfNotification.LIKE)
                        .createdAt(LocalDateTime.now())
                        .postId(postOptional.get().getId())
                        .build());

                System.out.println("like saved");
            } else{
                System.out.println("post not fount");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    public void unlikePost(Integer postId, Integer userId) {
        likeRepository.deleteByUserIdAndPostId(userId, postId);
        System.out.println("like deleted");
    }

    public boolean isPostLikedByUser(Integer userId, Integer postId) {
        return likeRepository.findByUserIdAndPostId(userId, postId).isPresent();
    }

    public Integer countLikes(Integer postId) {
        return likeRepository.countByPostId(postId);
    }

    public boolean isPostSavedByUser(Integer userId, Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(()->new UserNotFoundException("post not found"));
        return savePostRepository.existsByUserIdAndPost(userId,post);
    }
}
