package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialPlanModel {
    private String partCode;              // 자재코드(child_part_code)
    private String partName;              // 자재명(part.part_name)
    private Integer currInventory;        // 현재고
    private Integer expectedConsumption;  // 예상 소요량(작업지시 * useage 합)
    private Integer shortageQty;          // 부족량 = max(예상-현재고, 0)
    private String stockStatus;           // '부족' | '여유' (safe_qty 기준)
    private String orderRecommendation;
}


