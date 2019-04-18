package com.example.demo.Repository;

import com.example.demo.Entity.OrderMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 订单查询
 */
public interface OrderMasterRepository extends JpaRepository<OrderMaster,String> ,JpaSpecificationExecutor<OrderMaster> {

    List<OrderMaster> findAllByBuyerOpenid(String openid);
    List<OrderMaster> findAllByOrderId(String orderId);

}