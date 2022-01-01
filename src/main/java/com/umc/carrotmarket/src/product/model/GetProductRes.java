package com.umc.carrotmarket.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//가져와야하는 상품 정보
@Getter
@Setter
@AllArgsConstructor
public class GetProductRes {
    private int userIdx;
    private String name;
    private double temperature;
    private String region;
    private String title;
    private String content;
    private String imgUrl;
    private int viewCount;
    private String category;
    private int price;
    private String recentDate;
    private int likeCount;
    private int roomCount;

}
