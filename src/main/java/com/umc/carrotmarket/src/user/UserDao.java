package com.umc.carrotmarket.src.user;

import com.umc.carrotmarket.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkValidUser(int userIdx){  //탈퇴 상태인지 아닌지 check
        return this.jdbcTemplate.queryForObject("select exists(select status from User where userIdx = ? and status = ?)",
                int.class, userIdx, "inactive");     //존재하지 않음(False,0),존재함(True, 1)

    }
    public int addUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (name, password, email, phoneNum) VALUES (?,?,?,?)";
        Object[] createUserParams = new Object[]{
                postUserReq.getUserName(), postUserReq.getPassword(), postUserReq.getEmail(), postUserReq.getPhoneNum()
        };
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);      // bulk insert 한 첫 번째 row의 키값을 조회
    }

    public GetUserRes getUser(int userIdx){
        return this.jdbcTemplate.queryForObject("select * from User where userIdx = ?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("name"),
                        rs.getString("imgUrl"),
                        rs.getDouble("mannerTemp"),
                        rs.getDouble("tradeRate"),
                        rs.getDouble("responseRate")), userIdx
        );
    }

    public List<GetUserRes> getUserList(){
        return this.jdbcTemplate.query("Select * from User",
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("name"),
                        rs.getString("imgUrl"),
                        rs.getDouble("mannerTemp"),
                        rs.getDouble("tradeRate"),
                        rs.getDouble("responseRate"))
        );
    }

    public List<GetUserRes> getUserByName(String name){
        String wrappedKey = "%" + name + "%";
        return this.jdbcTemplate.query("select * from User where name like ?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getString("name"),
                        rs.getString("imgUrl"),
                        rs.getDouble("mannerTemp"),
                        rs.getDouble("tradeRate"),
                        rs.getDouble("responseRate")), wrappedKey
        );
    }

    //이메일 중복 체크(exist)
    public int checkEmail(String email){
        return this.jdbcTemplate.queryForObject("select exists(select email from User where email = ?)",
                 int.class, email);
    }


    //로그인 => 암호화된 비번 가져오기(걍 email 로 찾기네)
    //반환값에 userIdx 가 들어가야, 서비스 단에서 받아서 idx 로 찾은 비번이랑 같은지 비교
    //(Query, 객체 매핑 정보, Params)
    public User getPassword(PostLoginReq postLoginReq){

        return this.jdbcTemplate.queryForObject("select userIdx, password,email,name from User where email = ?",
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name")
                ),
                postLoginReq.getEmail()
        );
    }

    //회원 정보 수정
    //Post, Patch => jdbcTemplate.update
    public int modifyUserName(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set name = ? where userIdx = ?";
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getUserIdx()};

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); //대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    //회원 탈퇴시 이름도 (탈퇴)로 수정
    public int modifyUserStatus(int userIdx, String originName){
        String modifyUserNameQuery = "update User set status = ?, name = ? where userIdx = ?";
        Object[] modifyUserNameParams = new Object[]{"inactive", originName+"(탈퇴)", userIdx};

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams);
    }

}

