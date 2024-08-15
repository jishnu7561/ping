package com.ping.postservice.repository;

import com.ping.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {

    Integer countByUserId(Integer userId);

    Page<Post> findAllByUserId(Integer id,Pageable pageable);

    void deleteById(Integer postId);

    Page<Post> findByCaptionContainingIgnoreCase(String search, Pageable pageable);

    Page<Post> findByTagContainingIgnoreCase(String search, Pageable pageable);

    Page<Post> findByCaptionContainingIgnoreCaseAndTagContainingIgnoreCase(String caption,String tag, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate")
    Page<Post> findRecentPosts(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate AND (LOWER(p.caption) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.tag) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Post> findRecentPostsByCaptionOrTag(@Param("startDate") LocalDateTime startDate, @Param("search") String search, Pageable pageable);
}
