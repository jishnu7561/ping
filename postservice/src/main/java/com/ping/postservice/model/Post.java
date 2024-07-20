package com.ping.postservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table( name = "posts" )
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String caption;

    private String tag;

    @Column(name = "images")
    @OneToMany(mappedBy = "post" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Image> images;

    @OneToMany(mappedBy = "post" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Like> likes;

    @OneToMany(mappedBy = "post" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY , orphanRemoval = true)
    private List<SavedPost> savedPosts;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY , orphanRemoval = true)
    private List<ReportPost> reportPost;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
