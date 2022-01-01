package com.umc.carrotmarket.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductChatInfoRes {
    private String userName;
    private double mannerTemperature;
    private String userImg;
    private String productImg;
    private int price;
    private String productStatus;
    private String productTitle;
}
