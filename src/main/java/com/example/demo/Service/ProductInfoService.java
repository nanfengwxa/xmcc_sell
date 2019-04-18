package com.example.demo.Service;

import com.example.demo.Entity.ProductInfo;
import com.example.demo.commen.ResultResponse;

public interface ProductInfoService {

    ResultResponse queryList();
    //根据商品id查询商品信息
    ResultResponse<ProductInfo> queryById(String productId);

    void updateProduct(ProductInfo productInfo);


}
