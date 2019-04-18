package com.example.demo.Dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class orderDto {
    @NotNull(message = "不能为空")
    String openid;
    @NotNull(message = "不能为空")
    String orderId;
}
