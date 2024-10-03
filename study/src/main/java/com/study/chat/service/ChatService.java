package com.study.chat.service;

import com.study.chat.dto.ChatMsgDto;
import com.study.chat.dto.ChatRoomResDto;
import com.study.chat.entity.ChatRoom;
import com.study.chat.mapper.ChatRoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRoomMapper chatRoomMapper;
    private final Map<String, List<WebSocketSession>> roomSessionsMap = new ConcurrentHashMap<>();
    // 방과 참가자 목록을 관리하는 맵
    private Map<String, List<String>> rooms;

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

    // 채팅방 목록 조회
    public List<ChatRoom> findAll() {
        return chatRoomMapper.findAll();
    }

    // 채팅방 조회
    public ChatRoom findById(String roomId) {
        return chatRoomMapper.findById(roomId);
    }

    // 채팅방 ID 생성
    private String generateRoomId() {
        return UUID.randomUUID().toString();
    }

    // 사용자 채팅방 참여
    public void joinChatRoom(String roomId, String username) {
        // 사용자 정보를 DB에 저장하는 로직 추가 (필요 시)
        // chatSessionMapper.saveUserToRoom(roomId, username);
    }

    // 사용자 채팅방 나가기
    public void leaveChatRoom(String roomId, String username) {
        // 사용자 정보를 DB에서 삭제하는 로직 추가 (필요 시)
        // chatSessionMapper.removeUserFromRoom(roomId, username);
    }

    // 채팅 메시지 전송
    public void sendMessage(String roomId, String sender, String content) {
        ChatMsgDto chatMsg = new ChatMsgDto();
        chatMsg.setSender(sender);
        chatMsg.setMsg(content);
        chatMsg.setCreatedAt(LocalDateTime.now());

        saveMessage(content, sender, chatMsg.getMsg()); // 메시지 저장
        sendMsgToAll(roomId, chatMsg); // 모든 세션에 메시지 전송
    }

    // 메시지 저장
    public void saveMessage(String content, String sender, String msg) {
        // 메시지를 DB에 저장하는 로직 추가
        chatRoomMapper.saveMessage(content, sender); // 가정된 메서드
    }

    // 파일 업로드
    public String uploadFile(MultipartFile file) throws IOException {
        String uploadDir = "/uploads/"; // 파일 업로드 경로 설정
        File destFile = new File(uploadDir + file.getOriginalFilename());
        file.transferTo(destFile); // 파일 저장
        return destFile.getAbsolutePath(); // 저장된 파일 경로 반환
    }

    // 세션 추가 및 입장 처리
    public void addSessionAndHandleEnter(String roomId, WebSocketSession session) {
        roomSessionsMap.computeIfAbsent(roomId, k -> new java.util.ArrayList<>()).add(session);
    }

    // 세션 제거 및 퇴장 처리
    public void removeSessionAndHandleExit(String roomId, WebSocketSession session) {
        List<WebSocketSession> sessions = roomSessionsMap.get(roomId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                roomSessionsMap.remove(roomId);
            }
        }
    }

    // 모든 세션에 메시지 전송
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
    }

    // 인원 수
    public List<String> getParticipants(String roomId) {
        // roomId에 해당하는 방이 존재하는지 확인
        if (rooms.containsKey(roomId)) {
            return rooms.get(roomId);
        } else {
            // 방이 없으면 빈 리스트를 반환
            return List.of();
        }
    }

    public <T> void sendMessage(WebSocketSession session, T message) {
    }
}
