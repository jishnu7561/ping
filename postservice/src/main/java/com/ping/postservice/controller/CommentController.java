package com.ping.postservice.controller;

import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.CommentRequest;
import com.ping.postservice.dto.CommentResponse;
import com.ping.postservice.model.Comment;
import com.ping.postservice.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/addComments")
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request,
                                                      @RequestHeader("Authorization") String header) {
        return ResponseEntity.ok(commentService.addComments(request,header));
    }

    @PostMapping("/addReply")
    public ResponseEntity<CommentResponse> addReply(@RequestBody CommentRequest request,
                                                      @RequestHeader("Authorization") String header) {
        return ResponseEntity.ok(commentService.addReply(request,header));
    }

    @GetMapping("/getCommentsByPostId/{postId}")
    public ResponseEntity<List<CommentRequest>> getCommentsByPostId(@PathVariable Integer postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }
}
