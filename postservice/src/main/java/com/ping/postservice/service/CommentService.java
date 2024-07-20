package com.ping.postservice.service;

import com.ping.common.dto.Notification;
import com.ping.common.dto.TypeOfNotification;
import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.CommentRequest;
import com.ping.postservice.dto.CommentResponse;
import com.ping.postservice.dto.User;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.kafka.KafkaMessagePublisher;
import com.ping.postservice.model.Comment;
import com.ping.postservice.model.Post;
import com.ping.postservice.repository.CommentRepository;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.util.TimeAgoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TimeAgoUtil timeAgoUtil;

    @Autowired
    private KafkaMessagePublisher kafkaMessagePublisher;

    public CommentResponse addComments(CommentRequest request, String header) {
        System.out.println("comments: " +request);
        try {
            Optional<Post> postOptional = postRepository.findById(request.getPostId());
            User user = userClient.getUser(header).getBody();
            if(postOptional.isPresent() && user != null) {
                Comment comment1 = Comment.builder()
                        .post(postOptional.get())
                        .comment(request.getComment())
                        .userId(user.getId())
                        .createdAt(LocalDateTime.now())
                        .build();
                Comment comment = commentRepository.save(comment1);

                kafkaMessagePublisher.sendNotification("notification", Notification.builder()
                                .sender(user.getId())
                                .receiver(postOptional.get().getUserId())
                                .typeOfNotification(TypeOfNotification.COMMENT)
                                .createdAt(LocalDateTime.now())
                                .postId(postOptional.get().getId())
                                .commentId(comment.getId())
                                .build());

                return CommentResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .description("successfully added the comment")
                        .commentRequest(getCommentsByPostId(postOptional.get().getId()))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }catch (UserNotFoundException e){
            throw new UserNotFoundException(e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        } return CommentResponse.builder()
                .message("server error")
                .build();
    }

    private CommentRequest getNewComment(Comment comment1, User user) {
        return CommentRequest.builder()
                .commentId(comment1.getId())
                .comment(comment1.getComment())
                .parentId(comment1.getParentId())
                .created(timeAgoUtil.calculateTimeAgo(comment1.getCreatedAt()))
                .postId(comment1.getPost().getId())
                .user(user)
                .build();
    }

//    public List<CommentRequest> getCommentsByPostId(Integer postId) {
//        List<Comment> commentList = commentRepository.findAllCommentsByPostId(postId);
//        List<CommentRequest> commentRequestList = new ArrayList<>();
//        for (Comment comment : commentList){
//            commentRequestList.add(CommentRequest.builder()
//                            .commentId(comment.getId())
//                            .comment(comment.getComment())
//                            .parentId(comment.getParentId())
//                            .created(timeAgoUtil.calculateTimeAgo(comment.getCreatedAt()))
//                            .postId(comment.getPost().getId())
//                            .user(userClient.getUserIfExist(comment.getUserId()).getBody())
//                    .build());
//        }
//        return commentRequestList;
//    }

    public List<CommentRequest> getCommentsByPostId(Integer postId) {
        List<Comment> comments = commentRepository.findByPostIdAndParentIdIsNull(postId);
        return comments.stream().map(this::mapToCommentRequest).collect(Collectors.toList());
    }

//    private CommentRequest mapToCommentRequest(Comment comment) {
//        return CommentRequest.builder()
//                .commentId(comment.getId())
//                .comment(comment.getComment())
//                .postId(comment.getPost().getId())
//                .parentId(comment.getParentId())
//                .created(timeAgoUtil.calculateTimeAgo(comment.getCreatedAt()))
//                .user(userClient.getUserIfExist(comment.getUserId()).getBody()) // Assuming a method to get user details
//                .replies(comment.getReplies().stream().map(this::mapToCommentRequest).collect(Collectors.toList())) // Recursive mapping
//                .build();
//    }
private CommentRequest mapToCommentRequest(Comment comment) {
    List<CommentRequest> replies = new ArrayList<>();
    if (comment.getReplies() != null) {
        replies = comment.getReplies().stream()
                .map(this::mapToCommentRequest)
                .collect(Collectors.toList());
    }

    return CommentRequest.builder()
            .commentId(comment.getId())
            .comment(comment.getComment())
            .postId(comment.getPost().getId())
            .parentId(comment.getParentId())
            .created(timeAgoUtil.calculateTimeAgo(comment.getCreatedAt()))
            .user(userClient.getUserIfExist(comment.getUserId()).getBody()) // Assuming a method to get user details
            .replies(replies) // Use the initialized list to avoid null
            .build();
}

    public CommentResponse addReply(CommentRequest request, String header) {
        try {
            Optional<Post> postOptional = postRepository.findById(request.getPostId());
            System.out.println(request);
            User user = userClient.getUser(header).getBody();
            if(postOptional.isPresent() && user != null) {
                Comment comment1 = Comment.builder()
                        .post(postOptional.get())
                        .comment(request.getComment())
                        .parentId(request.getParentId())
                        .userId(user.getId())
                        .createdAt(LocalDateTime.now())
                        .build();
                commentRepository.save(comment1);
                return CommentResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("success")
                        .description("successfully added the comment")
//                        .commentRequest(getNewComment(comment1,user))
                        .commentRequest(getCommentsByPostId(postOptional.get().getId()))
                        .timestamp(LocalDateTime.now())
                        .build();
            }
        }catch (UserNotFoundException e){
            throw new UserNotFoundException(e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        } return CommentResponse.builder()
                .message("server error")
                .build();
    }
}
