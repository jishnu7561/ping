package com.ping.postservice.dto;

import com.ping.postservice.model.Post;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportPostRequest {

    private Integer postId;
    private Integer reporterId;
    private String reportDescription;
}
