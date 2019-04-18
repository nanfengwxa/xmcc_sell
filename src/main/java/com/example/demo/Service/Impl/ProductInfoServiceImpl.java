package com.example.demo.Service.Impl;

import com.example.demo.Dto.ProductCategoryDto;
import com.example.demo.Dto.ProductInfoDto;
import com.example.demo.Entity.ProductInfo;
import com.example.demo.Enums.ResultEnums;
import com.example.demo.Repository.ProductInfoRepository;
import com.example.demo.Service.ProductCategoryService;
import com.example.demo.Service.ProductInfoService;
import com.example.demo.commen.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;



import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j
public class ProductInfoServiceImpl implements ProductInfoService {
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductInfoRepository productInfoRepository;
    @Override
    public ResultResponse queryList() {
        ResultResponse<List<ProductCategoryDto>> categoryServiceResult = productCategoryService.findAll();
        List<ProductCategoryDto> categorydtoList = categoryServiceResult.getData();
        if(CollectionUtils.isEmpty(categorydtoList)){
            return categoryServiceResult;//如果分类列表为空 直接返回了
        }
        //获得类目编号集合
        List<Integer> categoryTypeList = categorydtoList.stream().map(categorydto -> categorydto.getCategoryType()).collect(Collectors.toList());
        //查询商品列表  这里商品上下架可以用枚举 方便扩展
        List<ProductInfo> productInfoList = productInfoRepository.findByProductStatusAndCategoryTypeIn(ResultEnums.PRODUCT_UP.getCode(), categoryTypeList);
        //多线程遍历 取出每个商品类目编号对应的 商品列表 设置进入类目中
        List<ProductCategoryDto> finalResultList = categorydtoList.parallelStream().map(categorydto -> {
            categorydto.setProductInfoDtoList(productInfoList.stream().
                    filter(productInfo -> productInfo.getCategoryType() == categorydto.getCategoryType()).map(productInfo ->
                    ProductInfoDto.build(productInfo)).collect(Collectors.toList()));
            return categorydto;
        }).collect(Collectors.toList());
        return ResultResponse.success(finalResultList);
    }


    //查询商品信息,并返回
    @Override
    public ResultResponse<ProductInfo> queryById(String productId) {
    log.info("传入参数-》{}",productId);

        if (StringUtils.isEmpty(productId)){
            return ResultResponse.fail(ResultEnums.PARAM_ERROR.getMsg());
        }
        Optional<ProductInfo> byId = productInfoRepository.findById(productId);
        ProductInfo productInfo1 = byId.get();
        log.info("{}",productInfo1.toString());
        if (!byId.isPresent()){
            return ResultResponse.fail(ResultEnums.NOT_EXITS.getMsg());
        }
        ProductInfo productInfo = byId.get();
        //判断是否下架
        if (productInfo.getProductStatus()==ResultEnums.PRODUCT_DOWN.getCode()){
            return ResultResponse.fail(ResultEnums.PRODUCT_DOWN.getMsg());
        }
        log.info("判断后-》{}",productInfo1.toString());
        return ResultResponse.success(productInfo);
    }

    //修改商品信息
    @Override
    public void updateProduct(ProductInfo productInfo) {
        productInfoRepository.save(productInfo);
    }
}
