package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Map;

// InspectionService.java
@Service
@RequiredArgsConstructor
public class InspectionService {
    private final RestTemplate restTemplate;

    // ① 라즈베리 응답 전체 반환 (권장)
    public Map<String, Object> callPiPredict(int count) {
        String url = "http://192.168.200.204:5005/api/predict/" + count;
        return restTemplate.getForObject(url, Map.class);
    }

    // ② 'runCount' 같은 단일 숫자가 꼭 필요하면 매핑 (임시로 actual_count 사용)
    public int extractRunCount(Map<String, Object> piResp, int fallback) {
        if (piResp == null) return fallback;
        Object v = piResp.get("actual_count"); // 또는 "request_count"
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(String.valueOf(v)); } catch (Exception ignore) {}
        return fallback;
    }
}

