package com.ping.postservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentRequest {

    private Integer commentId;
    private String comment;
    private Integer postId;
    private Integer parentId;
    private User user;
    private String created;
    private List<CommentRequest> replies;
}
