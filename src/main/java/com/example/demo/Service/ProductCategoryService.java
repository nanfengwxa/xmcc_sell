package com.example.demo.Service;

import com.example.demo.Dto.ProductCategoryDto;
import com.example.demo.commen.ResultResponse;

import java.util.List;

public interface ProductCategoryService {
    ResultResponse<List<ProductCategoryDto>> findAll();
}
