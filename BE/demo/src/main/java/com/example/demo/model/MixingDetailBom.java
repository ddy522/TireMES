package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * BOM 상세정보 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MixingDetailBom {

    /**
     * 부품코드 (부모)
     */
    private String partCode;

    /**
     * 자재부품코드 (자식)
     */
    private String childPartCode;

    /**
     * 자재부품명 (자식)
     */
    private String childPartName;

    /**
     * 사용량
     */
    private Double useage;
    /**
     * 필요량
     */
    private Double needQty;
    /**
     * 투입량
     */
    private Double inputQty;
}