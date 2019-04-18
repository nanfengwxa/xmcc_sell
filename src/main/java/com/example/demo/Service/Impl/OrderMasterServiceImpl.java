package com.example.demo.Service.Impl;
import com.example.demo.Dto.*;
import com.example.demo.Entity.OrderDetail;
import com.example.demo.Entity.OrderMaster;
import com.example.demo.Entity.ProductInfo;
import com.example.demo.Enums.OrderEnum;
import com.example.demo.Enums.PayEnum;
import com.example.demo.Enums.ProductEnums;
import com.example.demo.Enums.ResultEnums;
import com.example.demo.Repository.OrderDetailRepository;
import com.example.demo.Repository.OrderMasterRepository;
import com.example.demo.Service.OrderDetailService;
import com.example.demo.Service.OrderMasterService;
import com.example.demo.Service.ProductInfoService;
import com.example.demo.Utile.BigDecimalUtil;
import com.example.demo.Utile.CustomException;
import com.example.demo.Utile.UuidUtil;
import com.example.demo.commen.ResultResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class OrderMasterServiceImpl  implements OrderMasterService {


    @Autowired
    private OrderMasterRepository orderMasterRepository;
    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Override
    @Transactional

    public ResultResponse insertOrder(OrderMasterDto orderMasterDto) {
        //前面已经进行了参数校验 这儿不需要了  取出订单项即可
        List<OrderDetailDto> items = orderMasterDto.getItems();
        //创建订单detail 集合 将符合的放入其中 待会批量插入
        List<OrderDetail> orderDetailList = Lists.newArrayList();
        //创建订单总金额为0  涉及到钱的都用 高精度计算
        BigDecimal totalPrice = new BigDecimal("0");

        for (OrderDetailDto item : items) {
            ResultResponse<ProductInfo> resultResponse = productInfoService.queryById(item.getProductId());
            log.info("info -> {}",resultResponse.toString());
            log.info("{}",resultResponse.getCode());
            log.info("{}",item.getProductId());

            //说明该商品未查询到 生成订单失败，因为这儿涉及到事务 需要抛出异常 事务机制是遇到异常才会回滚
            if(resultResponse.getCode()== ResultEnums.FAIL.getCode()){
                throw new CustomException(resultResponse.getMsg());
            }
            //获得查询的商品
            ProductInfo productInfo = resultResponse.getData();
            //说明该商品 库存不足 订单生成失败 直接抛出异常 事务才会回滚
            if(productInfo.getProductStock()<item.getProductQuantity()){
                throw new CustomException(ProductEnums.PRODUCT_NOT_ENOUGH.getMsg());
            }
            //将前台传入的订单项DTO与数据库查询到的 商品数据组装成OrderDetail 放入集合中  @builder
            OrderDetail orderDetail = OrderDetail.builder().detailId(UuidUtil.getUuid()).productIcon(productInfo.getProductIcon())
                    .productId(item.getProductId()).productName(productInfo.getProductName())
                    .productPrice(productInfo.getProductPrice()).productQuantity(item.getProductQuantity())
                    .build();
            orderDetailList.add(orderDetail);
            //减少商品库存
            productInfo.setProductStock(productInfo.getProductStock()-item.getProductQuantity());
            productInfoService.updateProduct(productInfo);
            //计算价格
            totalPrice = BigDecimalUtil.add(totalPrice,BigDecimalUtil.multi(productInfo.getProductPrice(),item.getProductQuantity()));
        }
        //生成订单id
        String orderId = UuidUtil.getUuid();
        //构建订单信息  日期等都用默认的即可
        OrderMaster orderMaster = OrderMaster.builder().buyerAddress(orderMasterDto.getAddress()).buyerName(orderMasterDto.getName())
                .buyerOpenid(orderMasterDto.getOpenid()).orderStatus(OrderEnum.NEW.getCode())
                .payStatus(PayEnum.WAIT.getCode()).buyerPhone(orderMasterDto.getPhone())
                .orderId(orderId).orderAmount(totalPrice).build();
        //将生成的订单id，设置到订单项中
        List<OrderDetail> detailList = orderDetailList.stream().map(orderDetail -> {
            orderDetail.setOrderId(orderId);
            return orderDetail;
        }).collect(Collectors.toList());
        //插入订单项
        orderDetailService.batchInsert(detailList);
        //插入订单
        orderMasterRepository.save(orderMaster);
        HashMap<String, String> map = Maps.newHashMap();
        //按照前台要求的数据结构传入
        map.put("orderId",orderId);
        return ResultResponse.success(map);
    }

    @Override
    public ResultResponse orderdetail(orderDto orderDto) {
        //根据订单号查询
        List<OrderMaster> allByOrderId = orderMasterRepository.findAllByOrderId(orderDto.getOrderId());
        if (allByOrderId.size()==0||allByOrderId==null){
            throw new CustomException(OrderEnum.ORDER_NOT_EXITS.getMsg());
        }
        LookOrderMasterDto lookOrderMasterDto = new LookOrderMasterDto();
        for (OrderMaster orderMaster:allByOrderId){
            if (orderMaster.getOrderId().equals(orderDto.getOpenid())){
                throw new CustomException(OrderEnum.OPENID_ERROR.getMsg());
            }
            //封装dto

            BeanUtils.copyProperties(orderMaster,lookOrderMasterDto);
            //再通过获取到的订单号查询该订单下的所有订单项,并封装进dto的list中
            List<OrderDetail> byOrderId = orderDetailRepository.findByOrderId(orderDto.getOrderId());
                lookOrderMasterDto.setOrderDetailList(byOrderId);

        }
        return ResultResponse.success(lookOrderMasterDto);
    }

    @Override
    public ResultResponse ordercancel(orderDto orderDto) {
        //根据订单号查询
        List<OrderMaster> ByOrderId = orderMasterRepository.findAllByOrderId(orderDto.getOrderId());

        if (ByOrderId.size()==0||ByOrderId==null){
            throw new CustomException(OrderEnum.ORDER_NOT_EXITS.getMsg());
        }
        for (OrderMaster orderMaster:ByOrderId){
            if (orderMaster.getOrderId().equals(orderDto.getOpenid())){
                throw new CustomException(OrderEnum.OPENID_ERROR.getMsg());
            }
            orderMaster.setOrderStatus(OrderEnum.CANCEL.getCode());
            orderMasterRepository.save(orderMaster);


        }
return ResultResponse.success();
    }

    @Override
    public ResultResponse OrderMasterlist(ParamOrderMasterDto paramOrderMasterDto) {
        //通过openid查询所有订单，并转化为dto
        PageRequest pageable = new PageRequest(paramOrderMasterDto.getPage(), paramOrderMasterDto.getSize());
        Page<OrderMaster> page = orderMasterRepository.findAll(new Specification<OrderMaster>() {
            @Override
            public Predicate toPredicate(Root<OrderMaster> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<String> buyerOpenid = root.get("buyerOpenid");
                Predicate like = criteriaBuilder.like(buyerOpenid, paramOrderMasterDto.getOpenid());
                return like;
            }
        }, pageable);
        List<OrderMaster> collect = page.get().collect(Collectors.toList());
        ArrayList<LookOrderMasterDto> lookOrderMasterlist = new ArrayList<>();
        for (OrderMaster orderMaster:collect){
            LookOrderMasterDto lookOrderMasterDto = new LookOrderMasterDto();
            BeanUtils.copyProperties(orderMaster,lookOrderMasterDto);
            //再通过获取到的订单号查询该订单下的所有订单项,并封装进dto的list中
            List<OrderDetail> byOrderId = orderDetailRepository.findByOrderId(lookOrderMasterDto.getOrderId());
            lookOrderMasterDto.setOrderDetailList(byOrderId);
            lookOrderMasterlist.add(lookOrderMasterDto);
        }
        return ResultResponse.success(lookOrderMasterlist);
    }


}
