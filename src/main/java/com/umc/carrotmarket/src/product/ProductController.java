package com.umc.carrotmarket.src.product;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.config.BaseResponse;
import com.umc.carrotmarket.src.product.model.*;
import com.umc.carrotmarket.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.umc.carrotmarket.config.BaseResponseStatus.*;

//형식적 validation : 상품 추가와 삭제 전, 로그인이 되어 있는지 체크 / 제목/카테고리/내용이 null 값이 아닌지 check
@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final ProductProvider productProvider;
    private final JwtService jwtService;

    /**
     * 상품 추가
     * @param postProductReq
     * @return
     */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostProductRes> addProduct(@RequestBody PostProductReq postProductReq){
        try{
            if(postProductReq.getTitle() == null){
                return new BaseResponse<>(POST_POSTS_EMPTY_TITLE);
            }

            if(postProductReq.getContent() == null){
                return new BaseResponse<>(POST_POSTS_EMPTY_CONTENT);
            }

            if(postProductReq.getCategoryIdx() == 0){
                return new BaseResponse<>(POST_POSTS_EMPTY_CATEGORY);
            }

            postProductReq.setUserIdx(jwtService.getUserIdx());    //jwt
            PostProductRes postProductRes = productService.postProduct(postProductReq);
            return new BaseResponse<>(postProductRes);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 상품 상세페이지 => index
     * /product/:productId
     * @param productIdx
     * @return
     */
    @ResponseBody
    @GetMapping("/{productIdx}")
    public BaseResponse<GetProductRes> getProductInfo(@PathVariable("productIdx") int productIdx){
        try{
            GetProductRes product = productProvider.getProduct(productIdx);
            return new BaseResponse<>(product);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /***
     * 전체 상품 리스트 조회(paging 처리)
     * /products
     *
     * 상품 제목으로 조회
     * /products/?title="장갑"
     */

    //BaseResponse<List<GetProductResList>>
    @ResponseBody
    @GetMapping("")
    public BaseResponse<Map<String,Object>> getProductInfoList(@RequestParam(required = false) String title,
                                                               @RequestBody(required = false) PageInfoReq pageInfoReq){
        try{
            if(title != null){
                List<GetProductResList> products = productProvider.getProductsByName(title);     //이름으로 조회
                //return new BaseResponse<>(products);
            }
            //List<GetProductResList> products = productProvider.getProductList(pageInfoReq);            //전체 조회
            Map<String, Object> productList = productProvider.getProductList(pageInfoReq);
            return new BaseResponse<>(productList);
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 상품 수정
     */
    @ResponseBody
    @PatchMapping("/{productIdx}")
    public BaseResponse<String> updateProduct(@PathVariable("productIdx") int productIdx, @RequestBody(required = false) PatchProductReq updateReq){
        try{
            //업데이트와 삭제하려는 상품이, 현재 로그인한 사용자가 상품이 맞는지 확인
            int sellerId = productProvider.getProductSellerId(productIdx);
            if(jwtService.getUserIdx() != sellerId){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            if(updateReq != null){
                productService.updateProduct(new PatchProductReq(productIdx, updateReq.getTitle(), updateReq.getContent()
                        , updateReq.getImgUrl(), updateReq.getPrice(), updateReq.getCategoryIdx()));
                return new BaseResponse<>("상품 수정이 정상적으로 되었습니다.");
            }
            productService.deleteProduct(productIdx);
            return new BaseResponse<>("상품 삭제가 정상적으로 되었습니다.");
        }catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 상품 상세 페이지
     */
    @ResponseBody
    @GetMapping("/{productIdx}/chats")
    public BaseResponse<GetProductChatInfoRes> getProductChatInfo(@PathVariable("productIdx") int productIdx){
        try{
            GetProductChatInfoRes productChatInfo = productProvider.getProductChatInfo(productIdx);
            return new BaseResponse<>(productChatInfo);
        }catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

}
