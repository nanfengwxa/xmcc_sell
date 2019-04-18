package com.example.demo.Dto;

import com.example.demo.Entity.OrderDetail;
import com.example.demo.Entity.OrderMaster;
import lombok.Data;

import java.util.List;
@Data
public class LookOrderMasterDto extends OrderMaster {

    private List<OrderDetail> orderDetailList;
}
