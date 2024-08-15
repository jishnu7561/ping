package com.ping.postservice.controller;

import com.ping.postservice.GlobalException.Exceptions.UserNotFoundException;
import com.ping.postservice.dto.*;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.model.Post;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Page<PostResponse>> getUserPosts (@PathVariable("userId") Integer userId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostResponse> postResponseList = postService.getUserPosts(userId,pageable);
        return ResponseEntity.ok(postResponseList);
    }

    @DeleteMapping("/deletePost/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable Integer postId){
        System.out.println("called the delete method for deleting post");
        postRepository.deleteById(postId);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/getPostDetails/{postId}")
    public ResponseEntity<PostResponse> getPostDetails(@PathVariable Integer postId,@RequestHeader("Authorization") String header){

       return ResponseEntity.ok(postService.getPostDetails(postId,header));
    }

    @GetMapping("/getPostDetailsOfSaved/{postId}")
    public ResponseEntity<PostResponse> getPostDetailsOfSaved(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getPostDetailsOfSaved(postId));
    }

    @PostMapping("/editPost/{postId}")
    public ResponseEntity<BasicResponse> editPost(@PathVariable Integer postId,
                                                  @RequestHeader("Authorization") String header,
                                                  @RequestBody Map<String, String> requestBody){
        return ResponseEntity.ok(postService.editPost(postId,header,requestBody));
    }

    @GetMapping("/getPostCount")
    public ResponseEntity<Long> postCount(){
        return ResponseEntity.ok(postRepository.count());
    }

    @PostMapping("/delete-post")
    public ResponseEntity<BasicResponse> deletePost(@RequestParam Integer postId,
                                                    @RequestParam String reason,
                                                    @RequestParam Integer reportId) {
        System.out.println("reportId: "+ reportId +"  "+"postId: "+ postId +"  ");
        return ResponseEntity.ok(postService.deletePost(postId,reason,reportId));
    }

    @GetMapping("/post-details/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Integer id){
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping("/all-posts")
    public ResponseEntity<Page<PostDto>> getAllPost(@RequestParam(defaultValue = "") String search,
                                                    @RequestParam(defaultValue = "") String filter,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        System.out.println("search ==== "+search);
        System.out.println("filter ==== "+filter);
        System.out.println("page ==== "+page);
        System.out.println("size ==== "+size);
        return ResponseEntity.ok(postService.getAllPostsBasedOnSearch(search,filter,page,size));
    }
}
