// EquipmentMapper.java
package com.example.demo.mapper;

import com.example.demo.model.MetricModel;
import com.example.demo.model.MetricModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface EquipmentMapper {

    List<MetricModel> getMetricData(
            @Param("eqId") String eqId,
            @Param("metric") String metric,
            @Param("scale") String scale
    );
}
