package com.umc.carrotmarket.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostChatReq {
    //chat
    private int userIdx;
    private String message;
    private int roomIdx;
}
