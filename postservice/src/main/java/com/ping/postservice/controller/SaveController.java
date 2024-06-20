package com.ping.postservice.controller;

import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.PostResponse;
import com.ping.postservice.service.PostService;
import com.ping.postservice.service.SavePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post/save")
public class SaveController {

    @Autowired
    private SavePostService savePostService;

    @Autowired
    private PostService postService;

    @PostMapping("/savePost/{postId}")
    public ResponseEntity<BasicResponse> savePost (@PathVariable("postId") Integer postId,
                                                   @RequestHeader("Authorization") String header) {

        System.out.println("post id =" +postId);
        System.out.println("header =" +header);
        return ResponseEntity.ok(savePostService.savePost(postId,header));
    }

    @DeleteMapping("/unSavePost/{postId}")
    public ResponseEntity<BasicResponse> unSavePost (@PathVariable("postId") Integer postId,
                                                   @RequestHeader("Authorization") String header) {

        System.out.println("post id in delete =" +postId);
        System.out.println("header in delete =" +header);
        return ResponseEntity.ok(savePostService.unSavePost(postId,header));
    }

    @GetMapping("/getSavedPosts")
    public ResponseEntity<List<PostResponse>> getSavedPosts (@RequestHeader("Authorization") String header) {
        System.out.println("get post called ====");
        List<PostResponse> savedPosts = savePostService.getSavedPosts(header);
        return ResponseEntity.ok(savedPosts);
    }


}
