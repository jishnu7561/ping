package com.ping.postservice.service;

import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.PostResponse;
import com.ping.postservice.dto.User;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.model.Image;
import com.ping.postservice.model.Post;
import com.ping.postservice.model.SavedPost;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.repository.SavePostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SavePostService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private SavePostRepository savePostRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeService likeService;


//    @Autowired
//    private SavePostService savePostService;

    public BasicResponse savePost(Integer postId, String header) throws UserNotFoundException {
        try {
            User user = userClient.getUser(header).getBody();
            if(user != null) {
                System.out.println("user id"+ user);
                SavedPost savedPost = new SavedPost();
                savedPost.setPost(postRepository.findById(postId).orElseThrow(()->new UserNotFoundException("post not found")));
                savedPost.setUserId(user.getId());
                savedPost.setCreatedAt(LocalDateTime.now());
                savePostRepository.save(savedPost);
                return BasicResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .description("successfully saved")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

        } catch (UserNotFoundException ex){
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return BasicResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("failed to save")
                .description("server side error")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    public BasicResponse unSavePost(Integer postId, String header) throws UserNotFoundException {
        try {
            User user = userClient.getUser(header).getBody();
            if(user != null) {
                System.out.println("user id"+ user);
                Post post = postRepository.findById(postId).orElseThrow(()->new UserNotFoundException("post not found"));
                savePostRepository.deleteByUserIdAndPost(user.getId(),post);
                return BasicResponse.builder()
                        .status(HttpStatus.OK.value())
                        .message("Success")
                        .description("successfully saved")
                        .timestamp(LocalDateTime.now())
                        .build();
            }

        } catch (UserNotFoundException ex){
            throw ex;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return BasicResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("failed to save")
                .description("server side error")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public boolean isPostSavedByUser(Integer userId, Integer postId) {
        Post post = postRepository.findById(postId).orElseThrow(()->new UserNotFoundException("post not found"));
        return savePostRepository.existsByUserIdAndPost(userId,post);
    }


    public List<PostResponse> getSavedPosts(String header) throws UserNotFoundException {
        try {
            User user = userClient.getUser(header).getBody();
            if (user != null) {
               return findListOfSavedPost(user);
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    private List<PostResponse> findListOfSavedPost(User user) {
        List<SavedPost> savedPosts = savePostRepository.findAllByUserId(user.getId());
        List<PostResponse> postResponses = new ArrayList<>();
        if(!savedPosts.isEmpty()) {
            for(SavedPost savedPost : savedPosts) {
             postResponses.add(PostResponse.builder()
                        .postId(savedPost.getPost().getId())
                        .image(getPostImages(savedPost.getPost()))
                        .isLiked(likeService.isPostLikedByUser(user.getId(), savedPost.getPost().getId()))
                        .likeCount(likeService.countLikes(savedPost.getPost().getId()))
                        .isSaved(likeService.isPostSavedByUser(user.getId(),savedPost.getPost().getId()))
                        .build());
            }
        }
        return postResponses;
    }

    private List<String> getPostImages(Post post) {
        List<String> images = new ArrayList<>();
        for(Image image : post.getImages()) {
            images.add(image.getImageUrl());
        }
        return images;
    }
}
