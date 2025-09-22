package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InventoryLotTraceModel {
        private String lotNo;        // lot.lotno
        private String createdAt;    // 포맷 문자 (YYYY-MM-DD HH:mm)
        private Integer qty;         // lot.qty
}
