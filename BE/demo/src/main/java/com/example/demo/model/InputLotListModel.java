package com.example.demo.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InputLotListModel {
    private String lotno;
    private String partCode;
    private String partName;
    private int qty;
    private String createdAt;
    private String unitNm;
}
