package com.example.demo.Service;

import com.example.demo.Dto.ProductCategoryDto;
import com.example.demo.Entity.ProductCategory;
import com.example.demo.Repository.ProductCategoryRepository;
import com.example.demo.commen.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public ResultResponse<List<ProductCategoryDto>> findAll() {
        List<ProductCategory> productCategoryList = productCategoryRepository.findAll();
        //利用流转换为dto集合
        return ResultResponse.success(productCategoryList.stream().map(productCategory ->
                ProductCategoryDto.build(productCategory)
        ).collect(Collectors.toList()));
    }
}
