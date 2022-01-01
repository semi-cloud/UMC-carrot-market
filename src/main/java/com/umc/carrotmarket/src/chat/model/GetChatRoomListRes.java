package com.umc.carrotmarket.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatRoomListRes {
    private int roomIdx;   //검증 위해
    private String userName;
    private String userImg;
    private String userRegion;
    private String productImg;
    private String lastMessage;
    private String lastMessageTime;
}
