package com.study.chat.mapper;

import com.study.chat.dto.ChatMsgDto;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatSessionMapper {

    @Insert("INSERT INTO chat_message (room_id, sender, message, created_at) VALUES (#{roomId}, #{sender}, #{msg}, #{createdAt})")
    void insertMessage(ChatMsgDto chatMsg);

}
