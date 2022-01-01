package com.umc.carrotmarket.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatMessageRes {
    private String userImg;
    private String message;
    private String chatMessageTime;
}
