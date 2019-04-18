package com.example.demo.Service;

import com.example.demo.Dto.OrderMasterDto;
import com.example.demo.Dto.ParamOrderMasterDto;
import com.example.demo.Dto.orderDto;
import com.example.demo.Entity.OrderMaster;
import com.example.demo.commen.ResultResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderMasterService {

    ResultResponse insertOrder(OrderMasterDto orderMasterDto);

    ResultResponse orderdetail(orderDto orderDto);

    ResultResponse ordercancel(orderDto orderDto);

    //查看订单列表
     ResultResponse  OrderMasterlist(ParamOrderMasterDto paramOrderMasterDto);
}