package com.ping.postservice.repository;

import com.ping.postservice.model.Post;
import com.ping.postservice.model.SavedPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavePostRepository extends JpaRepository<SavedPost,Integer> {

    void deleteByUserIdAndPost(Integer id, Post post);

    boolean existsByUserIdAndPost(Integer userId, Post post);

    List<SavedPost> findAllByUserId(Integer id);
}
