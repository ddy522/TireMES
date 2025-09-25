package com.example.demo.service;

import com.example.demo.mapper.InspectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionOrchestrator {

    private final InspectionMapper inspectionMapper;

    @Transactional
    public void saveWorkNosToVisionResult(List<String> workNos) {
        inspectionMapper.insertVisionWorkNos(workNos);
    }
}

// 검사 시작 시 -> DB 저장 -> Pi 호출까지 같이
//@Service
//@RequiredArgsConstructor
//public class InspectionOrchestrator {
//    private final InspectionMapper inspectionMapper;
//    private final InspectionService inspectionService; // Pi 호출
//
//    @Transactional
//    public Map<String, Object> startInspection(List<String> workNos, int count) {
//        inspectionMapper.insertVisionWorkNos(workNos);   // ✅ DB insert
//        return inspectionService.callPiPredict(count);   // ✅ Pi 호출
//    }
//}
