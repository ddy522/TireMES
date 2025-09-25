package com.example.demo.controller;

import com.example.demo.model.MetricModel;
import com.example.demo.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class getMetricsController {

    private final EquipmentService service;

    @GetMapping("/api/equipment/{eqId}/metrics")
    public List<MetricModel> getMetricsController(
            @PathVariable String eqId,
            @RequestParam String metric, // 파라미터는 받지만 내부에서 무시됨
            @RequestParam String scale
    ) {
        // Service에서 metric 파라미터 무시하고 모든 gubun 데이터 반환
        return service.getMetricData(eqId, metric, scale);
    }
}