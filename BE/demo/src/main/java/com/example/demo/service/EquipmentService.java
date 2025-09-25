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
        // getAllMetricData가 아닌 getMetricData를 호출하고 metric을 null로 전달
        return mapper.getMetricData(eqId, null, scale);
    }
}