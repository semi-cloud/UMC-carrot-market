package com.umc.carrotmarket.src.product;

import com.umc.carrotmarket.config.BaseException;
import com.umc.carrotmarket.src.product.model.*;
import com.umc.carrotmarket.utils.PageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.umc.carrotmarket.config.BaseResponseStatus.DATABASE_ERROR;
import static com.umc.carrotmarket.config.BaseResponseStatus.PRODUCT_STATUS_DELETE;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProductProvider {

    private final ProductDao productDao;


    public GetProductRes getProduct(int productIdx) throws BaseException {
        try{
            if(productDao.checkValidProduct(productIdx) == 1){
                throw new BaseException(PRODUCT_STATUS_DELETE);
            }
            GetProductRes product = productDao.getProduct(productIdx);
            return product;
        }catch(Exception e) {
            log.info("Database error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetProductResList> getProductsByName(String title) throws BaseException {
        try{
            List<GetProductResList> productList = productDao.getProductByName(title);
            return productList;
        }catch(Exception e){
            log.info("Database error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //페이징 정보 + 데이터 정보(Map)
    public Map<String, Object> getProductList(PageInfoReq pageInfoReq)throws BaseException {
        Map<String, Object> resultMap = new HashMap<>();
        try{
            //paging 처리
            int size = productDao.getProductListSize();
            PageService pageService = new PageService(size, 4, 3);     //페이지 크기 설정 정보
            boolean hasNext = pageService.checkNextPage(pageInfoReq.getStart());

            PageInfoRes pageInfo = PageInfoRes.builder()
                    .currentPage(pageService.getPage())
                    .hasNext(hasNext)
                    .startPage(pageService.getStartPage())
                    .endPage(pageService.getEndPage())
                    .totalPage(pageService.getTotalPage())
                    .dataPerPage(pageService.getCountList()).build();

            List<GetProductResList> productList = productDao.getProductList(pageInfoReq.getStart()-1, pageService.getCountList());
            resultMap.put("dataList", productList);
            resultMap.put("pageInfo", pageInfo);

            return resultMap;
        }catch(Exception e){
            log.info("Database error = {}", e.getMessage());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductChatInfoRes getProductChatInfo(int productIdx) throws BaseException {
        try{
            GetProductChatInfoRes productChatInfo = productDao.getProductChatInfo(productIdx);
            return productChatInfo;
        }
        catch(Exception e){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getProductSellerId(int productIdx){
        return productDao.getProductSellerId(productIdx);
    }

}
