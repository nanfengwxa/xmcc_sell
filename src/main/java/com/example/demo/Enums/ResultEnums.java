package com.example.demo.Enums;

import lombok.Getter;

import javax.management.loading.MLetContent;

@Getter
public enum ResultEnums {

    SUCCESS(0,"成功"),
    FAIL(1,"失败"),
    PRODUCT_UP(0,"正常"),
    PRODUCT_UDOWN(0,"下架");

    private int code;
    private String msg;

    ResultEnums(int code,String msg) {
        this.code = code;
        this.msg = msg;
    }
}
