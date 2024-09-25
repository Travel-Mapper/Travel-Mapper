package com.study.chat.service;

import com.study.chat.dto.ChatMsgDto;
import com.study.chat.dto.ChatRoomResDto;
import com.study.chat.entity.ChatRoom;
import com.study.chat.mapper.ChatRoomMapper;
import com.study.chat.mapper.ChatSessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomMapper chatRoomMapper;
    private final ChatSessionMapper chatSessionMapper;
    private final Map<String, List<WebSocketSession>> roomSessionsMap = new ConcurrentHashMap<>();


    // 채팅방 개설
    public ChatRoomResDto createChatRoom(String roomName) {
        ChatRoomResDto room = new ChatRoomResDto();
        room.setRoomName(roomName);
        // roomId와 createdAt 등을 여기서 설정
        room.setRoomId(generateRoomId());
        room.setCreatedAt(LocalDateTime.now());
        chatRoomMapper.createRoom(room);
        return room;
    }

    public List<ChatRoom> findAll() {
        return chatRoomMapper.findAll();
    }

    public ChatRoom findById(String roomId) {
        return chatRoomMapper.findById(roomId);
    }

    // 채팅방 ID 생성 로직 (간단하게 UUID로 생성)
    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }

    public void addSessionAndHandleEnter(String roomId, WebSocketSession session, ChatMsgDto chatMsg) {
        // 세션 추가
        roomSessionsMap.computeIfAbsent(roomId, k -> new java.util.ArrayList<>()).add(session);
        // DB에 새로운 메시지 저장
        chatSessionMapper.insertMessage(chatMsg);  // 예시, 실제로는 ChatMsgDto에 맞는 쿼리를 작성해야 함
    }

    public void removeSessionAndHandleExit(String roomId, WebSocketSession session, ChatMsgDto chatMsg) {
        // 세션 제거
        List<WebSocketSession> sessions = roomSessionsMap.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessionsMap.remove(roomId);
            }
        }
        // DB에 채팅방 퇴장 메시지 저장
        chatSessionMapper.insertMessage(chatMsg);  // 예시, 실제로는 ChatMsgDto에 맞는 쿼리를 작성해야 함
    }

    public void sendMsgToAll(String roomId, ChatMsgDto chatMsg) {
        List<WebSocketSession> sessions = roomSessionsMap.get(roomId);
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                try {
                    session.sendMessage(new TextMessage(chatMsg.getMsg()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        // DB에 메시지 저장
        chatSessionMapper.insertMessage(chatMsg);  // 예시, 실제로는 ChatMsgDto에 맞는 쿼리를 작성해야 함
    }


}
