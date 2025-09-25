// src/main/java/com/example/demo/service/VisionDecisionService.java
package com.example.demo.service;

import com.example.demo.model.VisionDecisionModel;
import com.example.demo.mapper.InspectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisionDecisionService {

    private final InspectionMapper inspectionMapper;

    /** work_no의 최신 비전 판정을 가져옴 (없으면 미판정으로 반환) */
    public VisionDecisionModel getLatestDecision(String workNo) {
        VisionDecisionModel dto = inspectionMapper.selectLatestVisionDecision(workNo);
        if (dto == null) {
            // 미판정 기본 응답
            return VisionDecisionModel.builder()
                    .workNo(workNo)
                    .inspectionResult(null)
                    .imageUrl(null)
                    .build();
        }
        return dto;
    }

    /** 숫자 판정만 필요할 때 편의 메서드 */
    public Integer getDecisionNumber(String workNo) {
        return getLatestDecision(workNo).getInspectionResult(); // 1/0/null
    }

    /** 텍스트(OK/NG/미판정)로 받고 싶을 때 */
    public String getDecisionText(String workNo) {
        return getLatestDecision(workNo).getDecisionText();
    }
}
