package com.ping.chat_service.dto;

import com.ping.chat_service.model.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatResponse {

    private List<Message> messageList;
    private String AccountName;
    private String imageUrl;

}
