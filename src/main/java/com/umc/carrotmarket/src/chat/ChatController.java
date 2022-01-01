package com.umc.carrotmarket.src.chat;

import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.config.BaseResponse;
import com.umc.carrotmarket.src.chat.model.*;
import com.umc.carrotmarket.src.product.ProductProvider;
import com.umc.carrotmarket.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umc.carrotmarket.config.BaseResponseStatus.EMPTY_JWT;
import static com.umc.carrotmarket.config.BaseResponseStatus.INVALID_USER_JWT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chats")
public class ChatController {

    private final ChatProvider chatProvider;
    private final ChatService chatService;
    private final ProductProvider productProvider;
    private final JwtService jwtService;

    /**
     채팅방 메시지 조회
     */
    @ResponseBody
    @GetMapping("/{roomIdx}")
    public BaseResponse<Map<String, Object>> getChatMessages(@PathVariable("roomIdx") int roomIdx){
        try{

            GetChatInfoRes chatProductInfo = chatProvider.showChatInfo(roomIdx);
            List<GetChatMessageRes> chatList = chatProvider.showChatList(roomIdx);

            //Map 에 담아서 정보 전달
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("productInfo", chatProductInfo);
            chatMap.put("chatInfo", chatList);
            return new BaseResponse<>(chatMap);

        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 채팅방 목록 조회 => 탈퇴 회원 이면 이름 옆에 (탈퇴 )붙이기
     */
    @ResponseBody
    @GetMapping("")
    public BaseResponse<List<GetChatRoomListRes>> getChatRooms() {
        try{
            int userIdx = jwtService.getUserIdx();
            if(userIdx == 0) {
                return new BaseResponse<>(INVALID_USER_JWT);    //jwt 통해서 현재 로그인 한 사용자 가져오기
            }
            List<GetChatRoomListRes> getChatRoomListRes = chatProvider.showChatRoomList(userIdx);
            return new BaseResponse<>(getChatRoomListRes);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 채팅 룸에 입성해서, 채팅 보내기
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostRoomRes> enterChatRoom(@RequestBody PostRoomReq postRoomReq){
        try{
            //seller id 설정
            int sellerIdx = productProvider.getProductSellerId(postRoomReq.getProductIdx());
            postRoomReq.setSellerIdx(sellerIdx);

            int userIdx = jwtService.getUserIdx();
            if(userIdx == 0){
                return new BaseResponse<>(EMPTY_JWT);
            }
            postRoomReq.setBuyerIdx(userIdx);     //jwt 통해서 현재 로그인 한 사용자 가져오기

            PostRoomRes postRoomRes = chatService.addChatRoom(postRoomReq);
            return new BaseResponse<>(postRoomRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 채팅 메시지 보내기
     */
    @ResponseBody
    @PostMapping("/{roomIdx}")
    public BaseResponse<PostChatRes> sendChats(@PathVariable("roomIdx") int roomIdx, @RequestBody PostChatReq postChatReq) {
        try {
            postChatReq.setRoomIdx(roomIdx);
            PostChatRes postChatRes = chatService.addChat(postChatReq);
            return new BaseResponse<>(postChatRes);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 채팅방 삭제하기
     */
    @ResponseBody
    @PatchMapping("/{roomIdx}")
    public BaseResponse<String> exitChatRoom(@PathVariable("roomIdx") int roomIdx){
        try{
            chatService.getOutChatRoom(roomIdx);
            return new BaseResponse<>("채팅방 나가기가 정상적으로 되었습니다.");
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

}

