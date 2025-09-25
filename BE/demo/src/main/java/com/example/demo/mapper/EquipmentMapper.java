package com.example.demo.mapper;

import com.example.demo.model.MetricModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EquipmentMapper {

    // 기존 메서드 (사용 안 함)
    List<MetricModel> getMetricData(
            @Param("eqId") String eqId,
            @Param("metric") String metric,
            @Param("scale") String scale
    );

    // 새로운 메서드 - 모든 gubun 데이터 반환
    List<MetricModel> getAllMetricData(
            @Param("eqId") String eqId,
            @Param("scale") String scale
    );
}
