package com.umc.carrotmarket.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private int userIdx;
    private String name;
    private String password;
    private String email;
}
