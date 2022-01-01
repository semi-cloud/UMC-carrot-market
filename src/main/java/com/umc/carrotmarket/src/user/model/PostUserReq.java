package com.umc.carrotmarket.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUserReq {
    private String userName;
    private String password;
    private String phoneNum;
    private String email;
}
