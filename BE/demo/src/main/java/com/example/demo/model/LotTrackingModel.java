package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LotTrackingModel {
    private String lotno;
    private String processCode;
    private String partCode;
    private String partName;
    private String processName;
    private String createdAt;
    private int qty;
}
