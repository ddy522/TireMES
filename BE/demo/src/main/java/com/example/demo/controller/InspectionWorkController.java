package com.example.demo.controller;

import com.example.demo.service.InspectionOrchestrator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/inspection")  // 공통 prefix
public class InspectionWorkController {

    private final InspectionOrchestrator orchestrator;

    // ✅ 선택된 작업지시를 vision_result에 insert
    @PostMapping("/start")
    public ResponseEntity<?> startInspection(@RequestBody WorkNoRequest req) {
        orchestrator.saveWorkNosToVisionResult(req.getWorkNos());
        return ResponseEntity.ok("Inserted " + req.getWorkNos().size() + " rows into vision_result");
    }

    @Data
    public static class WorkNoRequest {
        private List<String> workNos;
    }
}
