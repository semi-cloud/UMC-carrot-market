package com.umc.carrotmarket.src.product.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PatchProductReq {
    private int productIdx;   //변경하려는 상품 id
    private String title;
    private String content;
    private String imgUrl;
    private int price;
    private int categoryIdx;
}
