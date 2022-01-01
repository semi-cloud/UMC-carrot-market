package com.umc.carrotmarket.src.user;


import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.config.BaseResponse;
import com.umc.carrotmarket.src.user.model.*;
import com.umc.carrotmarket.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.umc.carrotmarket.config.BaseResponseStatus.*;
import static com.umc.carrotmarket.utils.ValidationRegex.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
// 컨트롤러 => 형식적 validation
public class UserController {

    private final UserProvider userProvider;
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * 회원가입
     */
    // return new BaseResponse<>(POST_USERS_EMPTY_EMAIL); => 왜 에러 ㅠㅠ
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> signup(@RequestBody PostUserReq postUserReq) {

        // email null 체크, 비밀번호, 전화번호 모두 체크해야함
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        //이메일 형식 체크
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        //비밀번호 형식 체크
        if (!isRegexPassword(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }

        //전화번호 형식 체크
        if (!isRegexPhoneNum(postUserReq.getPhoneNum())) {
            return new BaseResponse<>(POST_USERS_INVALID_PHONE_NUMBER);
        }

        try {
            PostUserRes postUserRes = userService.postUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인
     * 형식적 validation : 이메일과 비밀번호가 null 값이 아닌지
     * 논리적 validation : 탈퇴 유저(status = delete)인지 아닌지 check => UserProvider 에서!
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq){

        //이메일과 비밀번호 null 값 체크
        if (postLoginReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        if(postLoginReq.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }

        try {
            PostLoginRes postLoginRes = userProvider.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 회원 개인 조회()
     */
    @ResponseBody
    @GetMapping("/{userIdx}")
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx){
        try{
            GetUserRes user = userProvider.getUser(userIdx);
            return new BaseResponse<>(user);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 전체 회원 조회
     *
     * 회원 이름으로 조회
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetUserRes>> getUserByName(@RequestParam(required = false) String name){
        try{
            if(name != null){
                List<GetUserRes> users = userProvider.getUserByName(name);
                return new BaseResponse<>(users);
            }
            List<GetUserRes> users = userProvider.getUsers();
            return new BaseResponse<>(users);
        }catch(BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 회원 정보 수정(삭제와 구분하기 위한 쿼리 파라미터 필요?)
     * //BODY 에 정보 실어서 오기
     */
    @ResponseBody
    @PatchMapping("/{userIdx}")
    public BaseResponse<String> updateUserName(@PathVariable("userIdx") int userIdx, @RequestBody(required = false) User updateUser){
        try{

            int userIdxByJwt = jwtService.getUserIdx();

            //userIdx 와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            //같다면 유저네임 변경
            if(updateUser != null){                        //업데이트할 정보를 들고오면 수정
                userService.modifyUserName(new PatchUserReq(userIdx, updateUser.getName()));
                return new BaseResponse<>("회원 이름 수정이 정상적으로 되었습니다.");
            }
            userService.deleteUser(userIdx);
            return new BaseResponse<>("회원 탈퇴가 정상적으로 되었습니다.");     //성공
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 회원 탈퇴/삭제
     */
    @PatchMapping("/delete/{userIdx}")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx){
        try{
            userService.deleteUser(userIdx);
            return new BaseResponse<>("회원 탈퇴가 정상적으로 되었습니다.");     //성공
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());                //오류
        }
    }
}
