package com.umc.carrotmarket.src.chat;

import com.umc.carrotmarket.src.chat.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkInvalidRoom(int roomIdx){
        return this.jdbcTemplate.queryForObject("select exists(select status from Room where roomIdx = ? and status = ?)",
                int.class, roomIdx, "inactive");
    }
    public List<GetChatRoomListRes> showRoomList(int userIdx){
        return this.jdbcTemplate.query("select Room.roomIdx as roomId, U.name as userName, U.imgUrl as userImg, R.name as userRegion, P.imgUrl as productImg, " +
                "LastChatMessage.lastMessage as message, LastChatMessage.lastMessageTime as currentTime " +
                "from Room " +
                "inner join User U on U.userIdx = Room.sellerIdx " +
                "inner join Region R on R.userIdx = Room.sellerIdx " +
                "inner join Product P on P.productIdx = Room.productIdx " +
                "inner join " +
                "(select Chat.roomIdx as roomIdx, message as lastMessage, date_format(createAt,'%y-%m-%d') as lastMessageTime from Chat " +
                "inner join " +
                "    (select roomIdx, max(chatIdx) as currentMessageNo from Chat " +
                "    group by roomIdx) as CurrentMessage " +
                "on CurrentMessage.currentMessageNo = Chat.chatIdx) as LastChatMessage " +
                "on LastChatMessage.roomIdx = Room.roomIdx " +
                "where Room.buyerIdx = ? " +
                "order by LastChatMessage.lastMessageTime desc ",
                (rs, rowNum) ->  new GetChatRoomListRes(
                        rs.getInt("roomId"),
                        rs.getString("userName"),
                        rs.getString("userImg"),
                        rs.getString("userRegion"),
                        rs.getString("productImg"),
                        rs.getString("message"),
                        rs.getString("currentTime")), userIdx);

    }

    public int addChatMessage(PostChatReq postChatReq){
        String createChatQuery = "insert into Chat (roomIdx, message, userIdx) VALUES(?,?,?)";
        Object[] createChatParams = new Object[]{
                postChatReq.getRoomIdx(), postChatReq.getMessage(), postChatReq.getUserIdx()
        };
        this.jdbcTemplate.update(createChatQuery, createChatParams);

        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);      // bulk insert 한 첫
    }

    public int addChatRoom(PostRoomReq postRoomReq){
        String createRoomQuery = "insert into Room (productIdx, sellerIdx, buyerIdx) VALUES(?,?,?)";
        Object[] createRoomParams = new Object[]{
                postRoomReq.getProductIdx(), postRoomReq.getSellerIdx(), postRoomReq.getBuyerIdx()
        };
        this.jdbcTemplate.update(createRoomQuery, createRoomParams);

        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);      // bulk insert 한 첫
    }

    public GetChatInfoRes showChatInfo(int roomIdx){
        return this.jdbcTemplate.queryForObject("select U.name as userName, U.mannerTemp as mannerTemperature, U.imgUrl as userImg, " +
                "       Product.imgUrl as productImg, price, productStatus, title " +
                "from Product " +
                "inner join User U on U.userIdx = Product.userIdx " +
                "inner join Room R on R.productIdx = Product.productIdx " +
                "where roomIdx = ?",
                (rs, rowNum) -> new GetChatInfoRes(
                        rs.getString("userName"),
                        rs.getDouble("mannerTemperature"),
                        rs.getString("userImg"),
                        rs.getString("productImg"),
                        rs.getInt("price"),
                        rs.getString("productStatus"),
                        rs.getString("title")), roomIdx);
    }


    public List<GetChatMessageRes> showChatList(int roomIdx){
        return this.jdbcTemplate.query("select message, U.imgUrl as userImg, Chat.createAt as createDate from Chat " +
                "inner join Room R on R.roomIdx = Chat.roomIdx " +
                "inner join User U on U.userIdx = Chat.userIdx " +
                "where R.roomIdx = ? " +
                "order by Chat.createAt",
                (rs, rowNum) -> new GetChatMessageRes(
                        rs.getString("userImg"),
                        rs.getString("message"),
                        rs.getString("createDate")), roomIdx);
    }

    //채팅방의 상태 변경
    public int deleteChatRoom(int roomIdx){
        String modifyRoomQuery= "update Room set status = ? where roomIdx = ?";
        Object[] modifyRoomParams = new Object[]{"inactive", roomIdx };

        return this.jdbcTemplate.update(modifyRoomQuery, modifyRoomParams);
    }
}
