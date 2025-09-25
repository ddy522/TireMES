package com.example.demo.model;


import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.annotations.Mapper;

@Getter
@Setter
public class MetricModel {

    private String label;
    private Double value;


}
