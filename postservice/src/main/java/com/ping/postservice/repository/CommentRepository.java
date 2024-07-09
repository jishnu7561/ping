package com.ping.postservice.repository;

import com.ping.postservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findAllCommentsByPostId(Integer postId);

    List<Comment> findByPostIdAndParentIdIsNull(Integer postId);
}
