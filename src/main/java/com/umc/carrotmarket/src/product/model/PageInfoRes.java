package com.umc.carrotmarket.src.product.model;

import lombok.*;

@Getter
@Setter
@Builder
public class PageInfoRes {
    private boolean hasNext;
    private int startPage;
    private int endPage;
    private int currentPage;
    private int totalPage;
    private int dataPerPage;   //한 페이지 데이터 개수
}
