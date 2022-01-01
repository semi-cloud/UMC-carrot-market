package com.umc.carrotmarket.src.product;

import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.src.product.model.PatchProductReq;
import com.umc.carrotmarket.src.product.model.PostProductReq;
import com.umc.carrotmarket.src.product.model.PostProductRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.umc.carrotmarket.config.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {

    private final ProductDao productDao;

    //상품 등록
    public PostProductRes postProduct(PostProductReq postProductReq) throws BaseException {
        try{
            int productIdx = productDao.addProduct(postProductReq);
            return new PostProductRes(productIdx);
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //상품 수정
    public void updateProduct(PatchProductReq patchProductReq) throws BaseException {
        try{
            int result = productDao.modifyProductInfo(patchProductReq);
            if(result == 0){
                throw new BaseException(MODIFY_FAIL_PRODUCT);
            }
        }catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }


    //상품 삭제
    public void deleteProduct(int productIdx) throws BaseException {
        try{
            int result = productDao.modifyProductStatus(productIdx);
            if(result == 0){
                throw new BaseException(DELETE_FAIL_PRODUCT);
            }
        }catch(Exception e){
            log.info("error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
