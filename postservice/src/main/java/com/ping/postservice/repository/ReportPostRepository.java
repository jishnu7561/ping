package com.ping.postservice.repository;

import com.ping.postservice.model.ReportPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPostRepository extends JpaRepository<ReportPost,Integer> {

}
