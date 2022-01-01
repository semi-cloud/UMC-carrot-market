package com.umc.carrotmarket.src.user;

import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.config.secret.Secret;
import com.umc.carrotmarket.src.user.model.GetUserRes;
import com.umc.carrotmarket.src.user.model.PatchUserReq;
import com.umc.carrotmarket.src.user.model.PostUserReq;
import com.umc.carrotmarket.src.user.model.PostUserRes;
import com.umc.carrotmarket.utils.AES128;
import com.umc.carrotmarket.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umc.carrotmarket.config.BaseResponseStatus.*;

//service/provider 단에서, 논리적 validation 일어남(dao)
@Service
@RequiredArgsConstructor
@Transactional(readOnly = false)
public class UserService {

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;

    // 회원 가입
    public PostUserRes postUser(PostUserReq postUserReq) throws BaseException {

        //이메일 중복 확인(이미 가입되어 있는지)
        if(userProvider.checkEmail(postUserReq.getEmail()) == 1){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        String pwd;
        try {
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword()); // 암호화코드
            postUserReq.setPassword(pwd);   //암호화된 비밀번호로 변경
        } catch (Exception ignored) {      // 암호화가 실패시 에러
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        try{
            int userIdx = userDao.addUser(postUserReq);        //db에 저장
            return new PostUserRes(userIdx);
        }catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }

    //회원 정보 수정
    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        try{
            int result = userDao.modifyUserName(patchUserReq);
            if(result == 0){       // 변경 실패
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        }catch (Exception exception) {         //db에 이상
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //회원 탈퇴
    public void  deleteUser(int userIdx) throws BaseException {
        try{
            GetUserRes user = userDao.getUser(userIdx);
            int result = userDao.modifyUserStatus(userIdx, user.getUserName());
            if(result == 0){
                throw new BaseException(DELETE_FAIL_USERID);
            }
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
