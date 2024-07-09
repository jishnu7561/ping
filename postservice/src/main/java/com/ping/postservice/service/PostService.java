package com.ping.postservice.service;

import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.PostResponse;
import com.ping.postservice.dto.User;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.model.Image;
import com.ping.postservice.model.Post;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.service.firebase.ImageService;
import com.ping.postservice.util.TimeAgoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PostService {

    @Autowired
    private ImageService imageService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TimeAgoUtil timeAgoUtil;

    @Autowired
    private LikeService likeService;

    @Autowired
    private SavePostService savePostService;

    public BasicResponse uploadPost(MultipartFile[] images, String caption, String tag, User user) {

        try {
            // Create Post object
            Post post = Post.builder()
                    .userId(user.getId())
                    .caption(caption)
                    .tag(tag)
                    .createdAt(LocalDateTime.now())
                    .build();

            List<Image> imageList = new ArrayList<>();
            for(MultipartFile image:images) {

                // Upload the image and get the URL
                String imageUrl = imageService.upload(image);

                // Create the Image object
                Image img = Image.builder()
                        .imageUrl(imageUrl)
                        .post(post)
                        .build();


                imageList.add(img);
            }

            // Set the image list for the post
            post.setImages(imageList);


            // Save the post (cascading will save the images)
            postRepository.save(post);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("uploaded successfully")
                    .description("post created successfully")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception ex){
            throw new RuntimeException(ex.getMessage());
        }
    }

    public List<PostResponse> getAllPosts(Integer userId) {
        List<PostResponse> postResponses = new ArrayList<>();
        List<Post> posts = postRepository.findAll();
        for(Post post:posts) {
            User user = userClient.getUserIfExist(post.getUserId()).getBody();
            if(user != null) {
                postResponses.add(
                        PostResponse.builder()
                                .postId(post.getId())
                                .userId(user.getId())
                                .profileImage(user.getImageUrl())
                                .accountName(user.getAccountName())
                                .fullName(user.getFullName())
                                .image(getPostImages(post))
                                .caption(post.getCaption())
                                .tag(post.getTag())
                                .createdAt(timeAgoUtil.calculateTimeAgo(post.getCreatedAt()))
                                .isLiked(likeService.isPostLikedByUser(userId,post.getId()))
                                .isSaved(savePostService.isPostSavedByUser(userId,post.getId()))
                                .likeCount(likeService.countLikes(post.getId()))
                                .isSubscribed(user.isSubscribed())
                                .build()
                );
            }

        }
        return postResponses;
    }

    public Integer getPostCount(Integer userId) {
        return postRepository.countByUserId(userId);
    }

    public List<PostResponse> getUserPosts(Integer userId) {
        try {
//            User user = userClient.getUser(header).getBody();
            User user = userClient.getUserIfExist(userId).getBody();
            if (user != null) {
                return findListOfPosts(user);
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return null;
    }

    private List<PostResponse> findListOfPosts(User user) {
        List<Post> postList = postRepository.findAllByUserId(user.getId());
        List<PostResponse> postResponses = new ArrayList<>();
        if(!postList.isEmpty()) {
            for (Post post : postList) {

                postResponses.add(
                        PostResponse.builder()
                                .postId(post.getId())
                                .userId(user.getId())
                                .profileImage(user.getImageUrl())
                                .accountName(user.getAccountName())
                                .fullName(user.getFullName())
                                .image(getPostImages(post))
                                .caption(post.getCaption())
                                .tag(post.getTag())
                                .createdAt(timeAgoUtil.calculateTimeAgo(post.getCreatedAt()))
                                .isLiked(likeService.isPostLikedByUser(user.getId(), post.getId()))
                                .likeCount(likeService.countLikes(post.getId()))
                                .isSaved(savePostService.isPostSavedByUser(user.getId(),post.getId()))
                                .build()
                );

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

    public PostResponse getPostDetails(Integer postId,String header) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new UserNotFoundException("post not found"));
            User user = userClient.getUser(header).getBody();
            if(user == null) {
                throw new UserNotFoundException("user not found");
            }
            return PostResponse.builder()
                    .postId(post.getId())
                    .userId(user.getId())
                    .profileImage(user.getImageUrl())
                    .accountName(user.getAccountName())
                    .fullName(user.getFullName())
                    .image(getPostImages(post))
                    .caption(post.getCaption())
                    .tag(post.getTag())
                    .createdAt(timeAgoUtil.calculateTimeAgo(post.getCreatedAt()))
                    .isLiked(likeService.isPostLikedByUser(user.getId(), post.getId()))
                    .likeCount(likeService.countLikes(post.getId()))
                    .isSaved(savePostService.isPostSavedByUser(user.getId(), post.getId()))
                    .build();

        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public BasicResponse editPost(Integer postId, String header, Map<String,String > requestBody) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new UserNotFoundException("post not found"));
            User user = userClient.getUser(header).getBody();
            post.setCaption(requestBody.get("caption"));
            post.setTag(requestBody.get("tag"));
            postRepository.save(post);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("successfully edited")
                    .description("post update has been successfully completed")
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    public PostResponse getPostDetailsOfSaved(Integer postId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(() -> new UserNotFoundException("post not found"));
            User user = userClient.getUserIfExist(post.getUserId()).getBody();
            if(user == null) {
                throw new UserNotFoundException("user not found");
            }
            return PostResponse.builder()
                    .postId(post.getId())
                    .userId(user.getId())
                    .profileImage(user.getImageUrl())
                    .accountName(user.getAccountName())
                    .fullName(user.getFullName())
                    .image(getPostImages(post))
                    .caption(post.getCaption())
                    .tag(post.getTag())
                    .createdAt(timeAgoUtil.calculateTimeAgo(post.getCreatedAt()))
                    .isLiked(likeService.isPostLikedByUser(user.getId(), post.getId()))
                    .likeCount(likeService.countLikes(post.getId()))
                    .isSaved(savePostService.isPostSavedByUser(user.getId(), post.getId()))
                    .build();

        } catch (UserNotFoundException e) {
            throw new UserNotFoundException(e.getMessage());
        }
        catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
