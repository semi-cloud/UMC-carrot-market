package com.umc.carrotmarket.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostRoomRes {
    //private int userIdx;    //jwt : 로그인 유저 정보 가져오면됌
    private int roomIdx;
}
