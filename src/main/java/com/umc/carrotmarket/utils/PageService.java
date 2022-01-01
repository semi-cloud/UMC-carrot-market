package com.umc.carrotmarket.utils;

import lombok.Getter;
import lombok.Setter;

//클라이언트가 마지막 조회한 데이터 id 1을 전송 =>
//서버는 1-10번 데이터 + has_next(다음것이 있느냐 여부) 같이 전송
@Getter
@Setter
public class PageService {

    private int page;     //현재 페이지 번호
    private int countList;    //한 페이지에 출력될 게시물 수  5개
    private int countPage;    //한 화면에 출력될 페이지 수 3개
    private int totalPage;    //총 페이지 개수      2개
    private int totalCount;    //총 데이터 개수      10개
    private int startPage;    //시작 페이지
    private int endPage;      //끝나는 페이지

    public PageService(int totalCount, int countList, int countPage){
        this.totalCount = totalCount;
        this.countList = countList;
        this.countPage = countPage;
    }

    public boolean checkNextPage(int lastAccessData) {
        //1.총 페이지 개수 구하기
        totalPage = totalCount / countList;

        if (totalCount % countList > 0) {  //딱 나눠지지 않는 경우에만 페이지 1 추가
            totalPage++;
        }

        if (totalPage < page) {
            page = totalPage;
        }

        //2. 현재 페이지 번호 구하기
        page = lastAccessData / countList + 1;
        if (page == countList) {
            page -= 1;
        }

        if (page < totalPage) {
            return true;
        }
        return false;
    }

    public int getStartPage(){
        //3. 받은 데이터를 가지고, 시작페이지 - 끝 페이지 구하기
        startPage = ((page - 1) / countPage) * countPage + 1;
        return startPage;
    }

    public int getEndPage(){
        endPage = startPage + countPage - 1;

        if(endPage > totalPage){    //end page 보정 과정
            endPage = totalPage;
        }

        return endPage;
    }

}
