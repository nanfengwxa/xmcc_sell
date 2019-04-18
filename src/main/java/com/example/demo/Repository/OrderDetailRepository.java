package com.example.demo.Repository;

import com.example.demo.Entity.OrderDetail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 订单项查询
 */
public interface OrderDetailRepository extends JpaRepository<OrderDetail,String>  {

    List<OrderDetail> findByOrderId(String orderId);

}