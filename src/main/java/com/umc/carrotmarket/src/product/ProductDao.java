package com.umc.carrotmarket.src.product;

import com.umc.carrotmarket.src.chat.model.GetChatInfoRes;
import com.umc.carrotmarket.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkValidProduct(int productIdx){
        return this.jdbcTemplate.queryForObject("select exists(select postStatus from Product where productIdx = ? and postStatus = ?)",
                int.class, productIdx, "inactive");     //존재하지 않음(False,0),존재함(True, 1)

    }
    public int getProductSellerId(int productIdx){
        return this.jdbcTemplate.queryForObject("select userIdx from Product where productIdx = ?",
                int.class, productIdx);

    }
    public int addProduct(PostProductReq postProductReq) {
        String createProductQuery1 = "insert into Product (userIdx, title, content, imgUrl, price, categoryIdx) VALUES (?,?,?,?,?,?)";

        Object[] createProductParams = new Object[]{ postProductReq.getUserIdx(),
                postProductReq.getTitle(), postProductReq.getContent(), postProductReq.getImgUrl(), postProductReq.getPrice()
                , postProductReq.getCategoryIdx()
        };

        this.jdbcTemplate.update(createProductQuery1, createProductParams);
        return this.jdbcTemplate.queryForObject("select last_insert_id()", int.class);
    }

    public GetProductRes getProduct(int productIdx) {
        return this.jdbcTemplate.queryForObject("select title, content, price, Product.imgUrl as productImg, R.name as sellRegion, roomCnt, likesCnt, " +
                        "U.name as sellerName, U.userIdx as userIdx, U.imgUrl as userImg, U.mannerTemp as sellerMannerTemp, viewCount, C.name as category, " +
                        "case when TIMESTAMPDIFF(MINUTE, Product.createAt, now()) < 60 then concat(TIMESTAMPDIFF(MINUTE,Product.createAt, now()),'분 전' ) " +
                        "when TIMESTAMPDIFF(HOUR, now(), Product.createAt) < 24 then concat(TIMESTAMPDIFF(HOUR,Product.createAt, now()),'시간 전' ) " +
                        "when TIMESTAMPDIFF(DAY, Product.createAt, now()) >= 1 then concat(TIMESTAMPDIFF(DAY,Product.createAt, now()),'일 전' ) " +
                        "else DATE_FORMAT(Product.createAt, '%y년 %m월 %d') end as recentDate " +
                        "from Product " +
                        "inner join User U on U.userIdx = Product.userIdx " +
                        "inner join Category C on C.categoryIdx = Product.categoryIdx " +
                        "inner join Region R on R.userIdx = Product.userIdx " +
                        "left outer join ( " +
                        "select count(productIdx) as roomCnt, productIdx from Room group by Room.productIdx) as room " +
                        "on room.productIdx = Product.productIdx " +
                        "left outer join ( " +
                        "select count(productIdx) as likesCnt, productIdx from Likes group by Likes.productIdx) as likes " +
                        "on likes.productIdx = Product.productIdx " +
                        "where Product.productIdx = ? ",
                (rs, rowNum) -> new GetProductRes(
                        rs.getInt("userIdx"),
                        rs.getString("sellerName"),
                        rs.getDouble("sellerMannerTemp"),
                        rs.getString("sellRegion"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("productImg"),
                        rs.getInt("viewCount"),
                        rs.getString("category"),
                        rs.getInt("price"),
                        rs.getString("recentDate"),
                        rs.getInt("likesCnt"),
                        rs.getInt("roomCnt")), productIdx
        );
    }

    public int getProductListSize(){
        return this.jdbcTemplate.queryForObject("select count(*) from Product",
                int.class);
    }

    /*페이징 처리하려면 join해서 한번에 쿼리문 가져와야 할듯
    public List<GetProductResList> getProductByName(String title) {
        String wrappedKeyword = "%" + title + "%";
        return this.jdbcTemplate.query("select productIdx, title, imgUrl, price, content," +
                        "case when TIMESTAMPDIFF(MINUTE, Product.createAt, now()) < 60 then concat(TIMESTAMPDIFF(MINUTE,Product.createAt, now()),'분 전' ) " +
                        "when TIMESTAMPDIFF(HOUR, now(), Product.createAt) < 24 then concat(TIMESTAMPDIFF(HOUR,Product.createAt, now()),'시간 전' ) " +
                        "when TIMESTAMPDIFF(DAY, Product.createAt, now()) >= 1 then concat(TIMESTAMPDIFF(DAY,Product.createAt, now()),'일 전' ) " +
                        "else DATE_FORMAT(Product.createAt, '%y년 %m월 %d') end as recentDate " +
                        "from Product " +
                        "where title like ?",
                (rs, rowNum) -> new GetProductResList(
                        rs.getInt("productIdx"),
                        rs.getString("title"),
                        rs.getString("content"),
                        rs.getString("imgUrl"),
                        rs.getInt("price"),
                        rs.getString("recentDate")), wrappedKeyword
        );
    }
    */

    public List<GetProductResList> getProductByName(String title) {
        String wrappedKeyword = "%" + title + "%";
        return this.jdbcTemplate.query("select Product.productIdx as productIdx, title, price, Product.imgUrl as productImg, R.name as sellRegion, roomCnt, likesCnt, " +
                        "case when TIMESTAMPDIFF(MINUTE, Product.createAt, now()) < 60 then concat(TIMESTAMPDIFF(MINUTE,Product.createAt, now()),'분 전' ) " +
                        "when TIMESTAMPDIFF(HOUR, now(), Product.createAt) < 24 then concat(TIMESTAMPDIFF(HOUR,Product.createAt, now()),'시간 전' ) " +
                        "when TIMESTAMPDIFF(DAY, Product.createAt, now()) >= 1 then concat(TIMESTAMPDIFF(DAY,Product.createAt, now()),'일 전' ) " +
                        "else DATE_FORMAT(Product.createAt, '%y년 %m월 %d') end as recentDate " +
                        "from Product " +
                        "inner join Region R on R.userIdx = Product.userIdx " +
                        "left outer join (" +
                        "     select count(productIdx) as roomCnt, productIdx from Room group by Room.productIdx) as room " +
                        "        on room.productIdx  = Product.productIdx " +
                        "left outer join ( " +
                        "      select count(productIdx) as likesCnt, productIdx from Likes group by Likes.productIdx) as likes " +
                        "        on likes.productIdx = Product.productIdx " +
                        "where title like ? ",
                (rs, rowNum) -> new GetProductResList(
                        rs.getInt("productIdx"),
                        rs.getString("sellRegion"),
                        rs.getString("title"),
                        rs.getString("productImg"),
                        rs.getInt("price"),
                        rs.getString("recentDate"),
                        rs.getInt("roomCnt"),
                        rs.getInt("likesCnt")), wrappedKeyword
        );
    }

    public List<GetProductResList> getProductList(int start, int offset) {

        return this.jdbcTemplate.query("select Product.productIdx as productIdx, title, price, Product.imgUrl as productImg, R.name as sellRegion, roomCnt, likesCnt," +
                "case when TIMESTAMPDIFF(MINUTE, Product.createAt, now()) < 60 then concat(TIMESTAMPDIFF(MINUTE,Product.createAt, now()),'분 전' ) " +
                "when TIMESTAMPDIFF(HOUR, now(), Product.createAt) < 24 then concat(TIMESTAMPDIFF(HOUR,Product.createAt, now()),'시간 전' ) " +
                "when TIMESTAMPDIFF(DAY, Product.createAt, now()) >= 1 then concat(TIMESTAMPDIFF(DAY,Product.createAt, now()),'일 전' ) " +
                "else DATE_FORMAT(Product.createAt, '%y년 %m월 %d') end as recentDate " +
                "from Product " +
                "inner join Region R on R.userIdx = Product.userIdx " +
                "left outer join ( " +
                "    select count(productIdx) as roomCnt, productIdx from Room group by Room.productIdx) as room " +
                "        on room.productIdx  = Product.productIdx " +
                "left outer join ( " +
                "    select count(productIdx) as likesCnt, productIdx from Likes group by Likes.productIdx) as likes " +
                "        on likes.productIdx = Product.productIdx " +
                "limit ? offset ? ",
                (rs, rowNum) -> new GetProductResList(
                        rs.getInt("productIdx"),
                        rs.getString("sellRegion"),
                        rs.getString("title"),
                        rs.getString("productImg"),
                        rs.getInt("price"),
                        rs.getString("recentDate"),
                        rs.getInt("roomCnt"),
                        rs.getInt("likesCnt")), offset, start
        );
    }

    public GetProductChatInfoRes getProductChatInfo(int productIdx){
        return this.jdbcTemplate.queryForObject("select U.name as userName, U.mannerTemp as mannerTemperature, U.imgUrl as userImg, " +
                        "       Product.imgUrl as productImg, price, productStatus, title " +
                        "from Product " +
                        "inner join User U on U.userIdx = Product.userIdx " +
                        "where productIdx = ?",
                (rs, rowNum) -> new GetProductChatInfoRes(
                        rs.getString("userName"),
                        rs.getDouble("mannerTemperature"),
                        rs.getString("userImg"),
                        rs.getString("productImg"),
                        rs.getInt("price"),
                        rs.getString("productStatus"),
                        rs.getString("title")), productIdx);

    }

    //제목, 내용, 가격, 카테고리, imgUrl
    //수정 성공하면 1, 성공 못하면 0 반환
    //postStatus => 상품 삭제 여부, productStatus => 판매중(sell)/거래완료(end)/숨김(hide)
    public int modifyProductInfo(PatchProductReq patchProductReq) {
        String modifyUserNameQuery = "update Product set title = ?, content = ?, price = ?, imgUrl = ?, categoryIdx = ? where productIdx = ?";
        Object[] modifyUserNameParams = new Object[]{patchProductReq.getTitle(),
            patchProductReq.getContent(), patchProductReq.getPrice(), patchProductReq.getImgUrl(), patchProductReq.getCategoryIdx(), patchProductReq.getProductIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }

    //상품 삭제
    public int modifyProductStatus(int productIdx) {
        String modifyUserNameQuery = "update Product set postStatus = ? where productIdx = ?";
        Object[] modifyUserNameParams = new Object[]{"inactive", productIdx };

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }

}
