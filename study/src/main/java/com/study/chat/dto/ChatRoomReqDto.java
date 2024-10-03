package com.study.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 채팅방 생성 요청 시 전달 되는 데이터
public class ChatRoomReqDto {
    private String name; // 해당 게시글의 제목
}