package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 작업지시서 상세정보 모델
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MixingDetailWorksheet {

    /**
     * 작업번호
     */
    private String workNo;

    /**
     * 작업상태
     */
    private String workState;

    /**
     * 수량
     */
    private Integer qty;

    /**
     * 부품코드
     */
    private String partCode;

    /**
     * 부품명
     */
    private String partName;

    private String worksheetSkey;
}