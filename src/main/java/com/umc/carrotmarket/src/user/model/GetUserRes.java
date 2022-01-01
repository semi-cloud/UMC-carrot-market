package com.umc.carrotmarket.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GetUserRes {

    private String userName;
    private String imgUrl;
    private double temperature;
    private double tradeRate;
    private double responseRate;
}
