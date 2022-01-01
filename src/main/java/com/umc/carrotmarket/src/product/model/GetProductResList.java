package com.umc.carrotmarket.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetProductResList {

    //담아올 데이터?
    private int productIdx;
    private String region;
    private String title;
    private String imgUrl;
    private int price;
    private String recentDate;
    private int likeCount;
    private int roomCount;


    //paging 처리
    //private boolean hasNext;

}
