package com.ping.postservice.dto;

import com.ping.postservice.model.Image;
import com.ping.postservice.model.Post;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data

public class PostDto {

    private Integer id;
    private String caption;
    private String tags;
    private List<String> imageUrls;
    private String profile;
    private String accountName;
    private int likesCount;


    public PostDto(Post post, String profile, String accountName,int count) {
        this.id = post.getId();
        this.caption = post.getCaption();
        this.tags = post.getTag();
        this.imageUrls = post.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList());
        this.profile = profile;
        this.accountName = accountName;
        this.likesCount = count;
    }

}
