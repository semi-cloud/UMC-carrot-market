package com.umc.carrotmarket.src.chat;

import com.umc.carrotmarket.src.chat.model.GetChatInfoRes;
import com.umc.carrotmarket.src.chat.model.GetChatMessageRes;
import com.umc.carrotmarket.src.chat.model.GetChatRoomListRes;
import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.src.user.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.umc.carrotmarket.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatProvider {

    private final ChatDao chatDao;
    private final UserDao userDao;

    /**
     판매자/상품 정보와 채팅 리스트를 나눈 이유?
     => 채팅 리스트는 여러갠데, 상품 정보는 한번만 불러와져야함(아니면 낭비 쿼리)
     */
    public GetChatInfoRes showChatInfo(int roomIdx) throws BaseException {
        //1.상품과 판매자 정보 가져오기
        try{
            GetChatInfoRes getChatInfoRes = chatDao.showChatInfo(roomIdx);
            return getChatInfoRes;
        }catch(Exception e){
            log.info("showChatInfo error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }

    }

    public List<GetChatMessageRes> showChatList(int roomIdx) throws BaseException {
        try{
            //논리적 검증 : 나간 채팅방인지
            if(!checkValidRoom(roomIdx)){
                throw new BaseException(ROOM_STATUS_DELETE);
            }

            List<GetChatMessageRes> chatMessageList = chatDao.showChatList(roomIdx);
            return chatMessageList;
        }catch(Exception e){
            log.info("showChatList error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    private boolean checkValidRoom(int roomIdx){
        if( chatDao.checkInvalidRoom(roomIdx) == 1){
            return false;
        }
        return true;
    }

    public List<GetChatRoomListRes> showChatRoomList(int userIdx) throws BaseException {
        try{
            //논리적 검증 : 탈퇴한 회원이면
            return chatDao.showRoomList(userIdx)
                    .stream()
                    .filter((x) -> checkValidRoom(x.getRoomIdx()))
                    .collect(Collectors.toList());
        }catch(Exception e){
            log.info("showChatRoomList error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
