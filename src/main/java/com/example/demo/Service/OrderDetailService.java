package com.example.demo.Service;

import com.example.demo.Entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    //批量插入
    void batchInsert(List<OrderDetail> orderDetailList);
}