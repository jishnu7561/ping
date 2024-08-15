package com.ping.chat_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Integer userId;
    private String fullName;
    private String accountName;
    private String email;
    private String bio;
    private boolean isBlocked;
    private String profileImage;
    private boolean isSubscribed;

    private Integer postId;
    private String caption;
    private String tag;
    private String createdAt;
    private List<String> image;
    private boolean isLiked;
    private Integer likeCount;
    private boolean isSaved;
    private String postImage;

}
