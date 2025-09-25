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
@RequiredArgsConstructor // 생성자 자동 생성
@CrossOrigin(origins = "http://localhost:5173") // Vite dev 서버 주소
public class getMetricsController {

    private final EquipmentService service; // 생성자 주입

    @GetMapping("/api/equipment/{eqId}/metrics")
    public List<MetricModel> getMetricsController(
            @PathVariable String eqId,
            @RequestParam String metric, // selectedMetric 값
            @RequestParam String scale   // chartScale 값
    ) {
        // Service 호출 → Mapper 통해 DB 조회
        return service.getMetricData(eqId, metric, scale);
    }
}
