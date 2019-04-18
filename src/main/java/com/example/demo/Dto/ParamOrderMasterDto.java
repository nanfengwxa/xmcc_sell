package com.example.demo.Dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ParamOrderMasterDto {
    @NotBlank(message = "openid")
    @ApiModelProperty(value = "用户授权号",dataType = "String")
    String openid;

    int page;
    int size;
}
