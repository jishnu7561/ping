package com.ping.postservice.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class UploadRequest {

    private String caption;
    private String tag;
    private List<MultipartFile> file;
}
