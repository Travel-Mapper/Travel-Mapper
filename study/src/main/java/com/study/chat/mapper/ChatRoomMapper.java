package com.study.chat.mapper;

import com.study.chat.dto.ChatRoomResDto;
import com.study.chat.entity.ChatRoom;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatRoomMapper {

    @Select("SELECT * FROM chat_room WHERE room_id = #{roomId}")
    ChatRoom findById(String roomId);

    @Select("SELECT * FROM chat_room")
    List<ChatRoom> findAll();

    @Insert("INSERT INTO chat_room (room_id, room_name, created_at) VALUES (#{roomId}, #{roomName}, #{createdAt})")
    void insert(ChatRoom chatRoom);

    @Update("UPDATE chat_room SET room_name = #{roomName} WHERE room_id = #{roomId}")
    void update(ChatRoom chatRoom);

    @Delete("DELETE FROM chat_room WHERE room_id = #{roomId}")
    void delete(String roomId);

    void createRoom(ChatRoomResDto room);
}
