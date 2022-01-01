package com.umc.carrotmarket.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

//상품 등록시 입력해야할 정보
@Getter
@Setter
@AllArgsConstructor
public class PostProductReq {

    private int userIdx;      //jwt에서 로그인 유저 가져오면 됌
    private String title;
    private String imgUrl;
    private int categoryIdx;
    private int price;
    private String content;

}
