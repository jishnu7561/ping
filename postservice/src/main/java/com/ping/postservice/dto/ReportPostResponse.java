package com.ping.postservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportPostResponse {

    private Integer reportId;
    private Integer postId;
    private String postImage;
    private Integer reporterId;
    private String reporterName;
    private String postUserName;
    private String reportDescription;
    private String createdAt;
}

