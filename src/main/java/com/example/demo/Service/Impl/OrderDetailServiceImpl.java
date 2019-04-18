package com.example.demo.Service.Impl;

import com.example.demo.Entity.OrderDetail;
import com.example.demo.Service.OrderDetailService;
import com.example.demo.Utile.AbstractBatchDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderDetailServiceImpl extends AbstractBatchDao<OrderDetail> implements OrderDetailService {

    //批量添加
    @Override
    @Transactional
    public void batchInsert(List<OrderDetail> orderDetailList) {
        super.batchInsert(orderDetailList);
    }
}
