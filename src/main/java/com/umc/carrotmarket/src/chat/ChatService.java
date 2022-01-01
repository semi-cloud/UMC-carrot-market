package com.umc.carrotmarket.src.chat;

import com.umc.carrotmarket.src.chat.model.PostChatReq;
import com.umc.carrotmarket.src.chat.model.PostChatRes;
import com.umc.carrotmarket.src.chat.model.PostRoomReq;
import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.src.chat.model.PostRoomRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umc.carrotmarket.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.carrotmarket.config.BaseResponseStatus.DELETE_FAIL_CHATROOM;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatService {

    private final ChatDao chatDao;

    //ROOM 입성
    public PostChatRes addChat(PostChatReq postChatReq) throws BaseException {
        try{
            //2.Chat 저장(roomIdx, message, userIdx)
            int chatIdx = chatDao.addChatMessage(postChatReq);
            return new PostChatRes(chatIdx);

        }catch (Exception e){
            log.info("addChat error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostRoomRes addChatRoom(PostRoomReq postRoomReq) throws BaseException {
        //1.Room 저장(productIdx, sellUser, buyUser)
        try{
            int roomIdx = chatDao.addChatRoom(postRoomReq);
            return new PostRoomRes(roomIdx);   //여기 바꾸기
        }catch(Exception e){
            log.info("addChatRoom error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void getOutChatRoom(int roomIdx) throws BaseException {
        try{
            int result = chatDao.deleteChatRoom(roomIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_CHATROOM);
            }
        }catch (Exception e){
            throw new BaseException(DATABASE_ERROR);
        }

    }

}
