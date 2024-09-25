package com.study.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMsgDto {
    public enum MessageType {
        ENTER, CHAT, CLOSE
    }

    private MessageType type;
    private String roomId;
    private String sender;
    private String msg;
    private LocalDateTime createdAt;  // 메시지 생성 시간
}