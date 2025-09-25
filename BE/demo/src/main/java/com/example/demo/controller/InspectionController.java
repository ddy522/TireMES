package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo.service.InspectionService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class InspectionController {
    private final InspectionService inspectionService;

    // 새로 추가: count만 받아 라즈베리 호출 후 결과 그대로 반환
    @PostMapping("/api/inspection/predict")
    public ResponseEntity<?> predict(@RequestBody Map<String, Object> body) {
        int count = ((Number) body.getOrDefault("count", 0)).intValue();
        Map<String, Object> piResp = inspectionService.callPiPredict(count);
        return ResponseEntity.ok(piResp);
    }

    // 기존 엔드포인트를 유지해야 한다면, 내부에서 위 메서드를 재사용해도 OK
    @PostMapping("/api/inspection/start/bulk-with-predict")
    public ResponseEntity<?> startBulkWithPredict(@RequestBody Map<String, Object> body) {
        int count = ((Number) body.getOrDefault("count", 0)).intValue();

        Map<String, Object> piResp = inspectionService.callPiPredict(count);
        int runCount = inspectionService.extractRunCount(piResp, count);

        // DB 업데이트를 파이가 한다면 여기서 더 할 일 없음. 필요시 piResp도 함께 내려줘.
        return ResponseEntity.ok(Map.of(
                "count", count,
                "runCount", runCount,
                "pi", piResp
        ));
    }
}
