package com.umc.carrotmarket.src.chat.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetChatInfoRes {
    /*
    select U.name as userName, U.mannerTemp as mannerTemperature, U.imgUrl as userImg,\n" +
                "       Product.imgUrl as productImg, price, productStatus, title
     */
    private String userName;
    private double mannerTemperature;
    private String userImg;
    private String productImg;
    private int price;
    private String productStatus;
    private String productTitle;
}
