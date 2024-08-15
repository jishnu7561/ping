package com.ping.postservice.service;

import com.ping.postservice.GlobalException.Exceptions.ResourceNotFoundException;
import com.ping.postservice.dto.BasicResponse;
import com.ping.postservice.dto.ReportPostRequest;
import com.ping.postservice.dto.ReportPostResponse;
import com.ping.postservice.feign.UserClient;
import com.ping.postservice.model.Post;
import com.ping.postservice.model.ReportPost;
import com.ping.postservice.repository.PostRepository;
import com.ping.postservice.repository.ReportPostRepository;
import com.ping.postservice.util.TimeAgoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportPostService {

    @Autowired
    private ReportPostRepository reportPostRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserClient userClient;

    @Autowired
    private TimeAgoUtil timeAgoUtil;


    @Transactional
    public BasicResponse saveReportPost(ReportPostRequest request) {
        try{

            Post post = postRepository.findById(request.getPostId()).orElseThrow(()->new ResourceNotFoundException("resource not found"));
            ReportPost reportPost = new ReportPost();
            reportPost.setPost(post);
            reportPost.setReportedAt(LocalDateTime.now());
            reportPost.setReportDescription(request.getReportDescription());
            reportPost.setReporterId(request.getReporterId());
            reportPostRepository.save(reportPost);
            return BasicResponse.builder()
                    .status(HttpStatus.OK.value())
                    .message("successfully done")
                    .description("Report post successfully done")
                    .build();
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }


    public List<ReportPostResponse> getAllPost() {
        List<ReportPost> reportPostList = reportPostRepository.findAll();
        List<ReportPostResponse> reportPostResponseList = new ArrayList<>();
        try {
            for (ReportPost reportPost : reportPostList) {

                ReportPostResponse response = ReportPostResponse.builder()
                        .reportId(reportPost.getId())
                        .reporterId(reportPost.getReporterId())
                        .postId(reportPost.getPost().getId())
                        .postImage(reportPost.getPost().getImages().get(0).getImageUrl())
                        .postUserName(userClient.getUserIfExist(reportPost.getPost().getUserId()).getBody().getAccountName())
                        .reporterName(userClient.getUserIfExist(reportPost.getReporterId()).getBody().getAccountName())
                        .reportDescription(reportPost.getReportDescription())
                        .createdAt(timeAgoUtil.changeTimeFormat(reportPost.getReportedAt()))
                        .build();

                reportPostResponseList.add(response);
            }
            return reportPostResponseList;
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
