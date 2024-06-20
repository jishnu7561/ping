package com.ping.postservice.controller;

import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.PostResponse;
import com.ping.postservice.dto.UploadRequest;
import com.ping.postservice.dto.User;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @PostMapping("/create-post/{id}")
    public ResponseEntity<?> createPost(@PathVariable("id") Integer userId,
                                                    @RequestParam("caption") String caption,
                                                    @RequestParam("tag") String tag,
                                                    @RequestParam("file") MultipartFile[] images) {


        // For now, we'll just print the data
        System.out.println("User ID: " + userId);
        System.out.println("Description: " + caption);
        System.out.println("Tag: " + tag);
        System.out.println("Number of images: " + images);
        try {
            // Check whether user exist or not
            User user = userClient.getUserIfExist(userId).getBody();

            if (user == null) {
                throw new UserNotFoundException("user not found");
            }

            // Upload the post details
            return ResponseEntity.ok(postService.uploadPost(images,caption,tag,user));

        } catch (UserNotFoundException ex) {
            throw new UserNotFoundException(ex.getMessage());
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//    @PostMapping("/create-post/{id}")
//    public ResponseEntity<String> createPost(
//            @PathVariable Long id,
//            @RequestBody UploadRequest uploadRequest // Accept an array of images
//    ) {
//        for (MultipartFile image : uploadRequest.getFile()) {
//            if (image != null && !image.isEmpty()) {
//                // Implement your file saving logic here
//                System.out.println("Saving file: " + image.getOriginalFilename());
//            }
//        }
//
//        // Save post details in the database
//        return ResponseEntity.ok("Post created successfully");
//    }


    @GetMapping("/getAllPosts/{userId}")
    public ResponseEntity<List<PostResponse>> getAllPosts(@PathVariable Integer userId) {
        System.out.println("Fetching all posts");
        return ResponseEntity.ok(postService.getAllPosts(userId));
    }

    @GetMapping("/getPostCount/{userId}")
    public ResponseEntity<Integer> getPostCount(@PathVariable Integer userId){
        return ResponseEntity.ok(postService.getPostCount(userId));
    }

    @GetMapping("/getUserPosts/{userId}")
    public ResponseEntity<List<PostResponse>> getUserPosts (@PathVariable("userId") Integer userId) {
        List<PostResponse> postResponseList = postService.getUserPosts(userId);
        return ResponseEntity.ok(postResponseList);
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Integer postId){
        System.out.println("called the delete method for deleting post");
        postRepository.deleteById(postId);
        return ResponseEntity.ok("success");
    }
}
