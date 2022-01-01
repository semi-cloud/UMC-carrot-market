package com.umc.carrotmarket.src.user;

import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.config.secret.Secret;
import com.umc.carrotmarket.src.user.model.*;
import com.umc.carrotmarket.utils.AES128;
import com.umc.carrotmarket.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.umc.carrotmarket.config.BaseResponseStatus.*;

//provider : select/read(조회)
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException{
        User user = userDao.getPassword(postLoginReq);   //암호화된 패스워드
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());  // 암호 복호화
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if(postLoginReq.getPassword().equals(password)) {   //성공하면 로그인 유저 id 반환
            int userIdx = userDao.getPassword(postLoginReq).getUserIdx();
            String jwt = jwtService.createJwt(userIdx);

            //탈퇴 상태 체크(status 가 delete 상태인 유저가 존재하면)
            if(checkIfValidUser(userIdx) == 1){
                throw new BaseException(USER_STATUS_DELETE);
            }

            return new PostLoginRes(jwt, userIdx);
        }else{
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public GetUserRes getUser(int idx) throws BaseException {
        try{
            return userDao.getUser(idx);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUsers() throws BaseException {
        try {
            return userDao.getUserList();
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserRes> getUserByName(String name) throws BaseException {
        try {
            return userDao.getUserByName(name);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 해당 이메일이 이미 User Table 에 존재하는지 확인(논리적 validation)
    public int checkEmail(String email) throws BaseException {
        try{
            return userDao.checkEmail(email);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkIfValidUser(int userIdx) throws BaseException {
        try{
            return userDao.checkValidUser(userIdx);
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
