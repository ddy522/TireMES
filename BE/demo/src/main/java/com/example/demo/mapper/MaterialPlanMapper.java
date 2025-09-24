package com.example.demo.mapper;

import com.example.demo.model.MaterialPlanModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MaterialPlanMapper {

    List<MaterialPlanModel> selectMaterialPlan(
            @Param("productCode") String productCode,   // 선택 제품 (옵션)
            @Param("fromDate") String fromDate,         // 기간 시작(YYYY-MM-DD) (옵션)
            @Param("toDate") String toDate              // 기간 종료(YYYY-MM-DD) (옵션)
    );
}
