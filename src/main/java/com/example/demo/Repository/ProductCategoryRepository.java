package com.example.demo.Repository;

import com.example.demo.Entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;


//第一个参数 是实体类名称  第二个参数是主键类型
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {

}
