package com.example.demo.Controller;


import com.example.demo.Dto.OrderMasterDto;
import com.example.demo.Dto.ParamOrderMasterDto;
import com.example.demo.Dto.orderDto;
import com.example.demo.Service.OrderMasterService;
import com.example.demo.Utile.JsonMapper;
import com.example.demo.commen.ResultResponse;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyer/order")
@Api(value = "订单相关接口",description = "完成订单的增删改查")
public class OrderMasterController {
    @Autowired
    private OrderMasterService orderMasterService;

    @PostMapping("/create")
    @ApiOperation(value = "创建订单接口", httpMethod = "POST", response =ResultResponse.class)
    public ResultResponse create(
            @Valid @ApiParam(name="订单对象",value = "传入json格式",required = true)
                    OrderMasterDto orderMasterDto, BindingResult bindingResult){
        Map<String,String> map = Maps.newHashMap();
        //判断是否有参数校验问题
        if(bindingResult.hasErrors()){
            List<String> errList = bindingResult.getFieldErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.toList());
            map.put("参数校验错误",JsonMapper.obj2String(errList));
            //将参数校验的错误信息返回给前台
            return  ResultResponse.fail(map);
        }
        return orderMasterService.insertOrder(orderMasterDto);
    }

    @GetMapping("/list")
    @ApiOperation(value = "订单查询接口", httpMethod = "GET", response =ResultResponse.class)
    public ResultResponse orderlist(ParamOrderMasterDto paramOrderMasterDto){


        return orderMasterService.OrderMasterlist(paramOrderMasterDto);
    }
    @GetMapping("/detail")
    @ApiOperation(value ="订单详情接口", httpMethod = "GET", response =ResultResponse.class)
    public  ResultResponse detail(orderDto orderDto){
        return orderMasterService.orderdetail(orderDto);
    }

    @PostMapping("/cancel")
    @ApiOperation(value = "取消订单接口", httpMethod = "POST", response =ResultResponse.class)
    public ResultResponse  cancel(orderDto orderDto){
        ResultResponse ordercancel = orderMasterService.ordercancel(orderDto);
        return ordercancel;
    }
}
