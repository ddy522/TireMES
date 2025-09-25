package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetricModel {
    private String label;      // 시간 레이블
    private Double value;      // 값
    private String gubun;      // gubun 정보 추가
}
