package com.study.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.study.chat.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Slf4j
//@NoArgsConstructor
public class ChatRoomResDto {
    private String roomId; // 채팅방 id
    private String roomName; // 채팅방 이름
    private LocalDateTime regDate; // 채팅방 생성 시간

    @JsonIgnore // WebSocketSession 직렬화 방지
    private Set<WebSocketSession> sessions;

    // 세션 수가 0인지 확인
    public boolean isSessionEmpty() {
        return this.sessions.size() == 0;
    }

    @Builder
    public ChatRoomResDto(String roomId, String roomName, LocalDateTime regDate) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.regDate = regDate;
        this.sessions = Collections.newSetFromMap(new ConcurrentHashMap<>());
    }

    public void handlerActions(WebSocketSession session, ChatMsgDto chatMessage, ChatService chatService) {
        if (chatMessage.getType() != null && chatMessage.getType().equals(ChatMsgDto.ChatType.ENTER)) {
            sessions.add(session);
//            if (chatMessage.getSender() != null) {
//                chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다.");
//            }

            log.debug("New session added : " + session);
        } else if (chatMessage.getType() != null && chatMessage.getType().equals(ChatMsgDto.ChatType.CLOSE)) {
            sessions.remove(session);
//            if (chatMessage.getSender() != null) {
//                chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장했습니다.");
//            }
            log.debug("Message removed : " + session);
        } else {
            // 입장과 퇴장이 아닌 경우 => 메세지를 보내는 경우 -> 보낼 때마다 메세지 저장
            chatService.saveMessage(chatMessage.getRoomId(), chatMessage.getSender(), chatMessage.getMsg());
            log.debug("Message received: " + chatMessage.getMsg());
        }

//        if (this.isSessionEmpty()) {
//            // 채팅방이 빈 상태이면 채팅방을 제거
//            chatService.removeRoom(this.roomId);
//        }
        sendMessage(chatMessage, chatService);
    }

    // 채팅방 세션 제거
    public void handleSessionClosed(WebSocketSession session, ChatService chatService) {
        sessions.remove(session); // removeAll?
        log.debug("Session closed: " + session);

//        if(this.isSessionEmpty()) {
//            // 채팅방이 빈 상태이면 채팅방을 제거
//            chatService.removeRoom(this.roomId);
//        }
    }

    private <T> void sendMessage(T message, ChatService chatService) {
        for (WebSocketSession session : sessions) {
            try {
                chatService.sendMessage(session, message);
            } catch (Exception e) {
                log.error("Error sending message in ChatRoomResDto: ", e);
            }
        }
    }

    public ChatRoomResDto() {
    }

    public void setCreatedAt(LocalDateTime now) {
    }
}