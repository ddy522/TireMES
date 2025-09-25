package com.example.demo.service;

import com.example.demo.mapper.EquipmentMapper;
import com.example.demo.model.MetricModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final EquipmentMapper mapper;

    public List<MetricModel> getMetricData(String eqId, String selectedMetric, String scale) {
// selectedMetric에 따라 DB 컬럼 이름 매핑
        String metricColumn;
        if ("temp".equalsIgnoreCase(selectedMetric)) {
            metricColumn = "DW1";
        } else if ("humid".equalsIgnoreCase(selectedMetric)) {
            metricColumn = "DW0";
        } else {
            throw new IllegalArgumentException("지원하지 않는 metric: " + selectedMetric);
        }

        return mapper.getMetricData(eqId, metricColumn, scale);
    }
}
