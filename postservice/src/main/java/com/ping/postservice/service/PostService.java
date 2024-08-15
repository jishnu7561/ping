package com.ping.postservice.service;

import com.ping.postservice.GlobalException.Exceptions.ResourceNotFoundException;
import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.PostDto;
import com.ping.postservice.dto.PostResponse;
import com.ping.postservice.dto.User;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.model.Image;
import com.ping.postservice.model.Post;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.repository.ReportPostRepository;
import com.ping.postservice.service.firebase.ImageService;
import com.ping.postservice.util.EmailUtil;
import com.ping.postservice.util.TimeAgoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private ReportPostRepository reportPostRepository;

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
        Collections.reverse(postResponses);
        return postResponses;
    }

    public Integer getPostCount(Integer userId) {
        return postRepository.countByUserId(userId);
    }

    public Page<PostResponse> getUserPosts(Integer userId,Pageable pageable) {
        try {
//            User user = userClient.getUser(header).getBody();
            User user = userClient.getUserIfExist(userId).getBody();
            if (user != null) {
                return findListOfPosts(user,pageable);
            }
        } catch (UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return Page.empty();
    }

    private Page<PostResponse> findListOfPosts(User user,Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByUserId(user.getId(),pageable);
        List<PostResponse> postResponses = new ArrayList<>();
        if(!postPage.isEmpty()) {
            for (Post post : postPage) {

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
        return new PageImpl<>(postResponses, pageable, postPage.getTotalElements());
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

    @Transactional
    public BasicResponse deletePost(Integer postId, String reason,Integer reportId) {
        try {
            Post post = postRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("post not found"));
            User user = userClient.getUserIfExist(post.getUserId()).getBody();
//            return new SendOtpResponse(true, "success");
            postRepository.deleteById(postId);
            reportPostRepository.deleteById(reportId);
            emailUtil.sendPostDeletedEmail(user.getEmail(),reason);
            return BasicResponse.builder()
                    .status( HttpStatus.OK.value())
                    .message("Success")
                    .description("Email send successfully")
                    .timestamp(LocalDateTime.now())
                    .build();

        }catch (ResourceNotFoundException ex) {
            throw new ResourceNotFoundException(ex.getMessage());
        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        } catch (MailException e) {
            return BasicResponse.builder()
                    .status( HttpStatus.CONFLICT.value())
                    .message(e.getMessage())
                    .description("Some Error occurred while sending OTP")
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            return BasicResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .description("server side error")
                    .timestamp(LocalDateTime.now())
                    .build();
        }

    }

    public PostResponse getPost(Integer id) {
        try{
            Post post = postRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("post not found"));
            PostResponse postResponse = PostResponse.builder()
                    .postImage(post.getImages().get(0).getImageUrl())
                    .postId(post.getId())
                    .userId(post.getUserId())
                    .build();
            return postResponse;
        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException(e.getMessage());
        } catch (RuntimeException e) {
            throw new RuntimeException("Internal server error");
        }
    }

    public Page<PostDto> getAllPostsBasedOnSearch(String search, String filter, int page, int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts;
            LocalDateTime startDate = LocalDateTime.now().minusDays(7);

            if (Objects.equals(search, "") && Objects.equals(filter, "")) {
                System.out.println("search1: "+search+" , "+"filter1: "+filter);
                posts = postRepository.findAll(pageable);
            }
            else if (!search.isEmpty() && filter.equals("content")) {
                System.out.println("search2: "+search+" , "+"filter2: "+filter);
                posts = postRepository.findByCaptionContainingIgnoreCase(search, pageable);
            }
            else if (!search.isEmpty() && filter.equals("tag")) {
                System.out.println("search3: "+search+" , "+"filter3: "+filter);
                posts = postRepository.findByTagContainingIgnoreCase(search, pageable);
            }
            else if (!search.isEmpty() && filter.equals("recent")) {
                System.out.println("search4: "+search+" , "+"filter4: "+filter);
                posts = postRepository.findRecentPostsByCaptionOrTag(startDate,search, pageable);
            }
            else if (search.isEmpty() && filter.equals("content")) {
                System.out.println("search5: "+search+" , "+"filter5: "+filter);
                posts = postRepository.findByCaptionContainingIgnoreCase(search, pageable);
            }
            else if (search.isEmpty() && filter.equals("tag")) {
                System.out.println("search6: "+search+" , "+"filter6: "+filter);
                posts = postRepository.findByTagContainingIgnoreCase(search, pageable);
            } else if (search.isEmpty() && filter.equals("recent")) {
                posts = postRepository.findRecentPosts(startDate, pageable);
            } else {
                posts = postRepository.findByCaptionContainingIgnoreCase(search, pageable);
            }
            List<PostDto> postDtos = posts.getContent().stream().map(post -> {
                // Fetch user details using userService
                User user = userClient.getUserIfExist(post.getUserId()).getBody();
                return new PostDto(post, user.getImageUrl(), user.getAccountName(),post.getLikes().size());
            }).collect(Collectors.toList());

            return new PageImpl<>(postDtos, pageable, posts.getTotalElements());
        } catch (Exception e){
            throw new RuntimeException("Internal server issue");
        }
    }
}
