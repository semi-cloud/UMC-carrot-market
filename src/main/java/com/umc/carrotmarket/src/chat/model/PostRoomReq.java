package com.umc.carrotmarket.src.chat.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostRoomReq {
    //room
    private int productIdx;
    private int sellerIdx;
    private int buyerIdx;   //jwt
}
