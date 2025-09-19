package com.example.demo.controller;

import com.example.demo.model.MaterialPlanModel;
import com.example.demo.service.MaterialPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/materials")
public class MaterialPlanController {

    private final MaterialPlanService materialPlanService;

    /**
     * 자재 계획(예상 소요량 vs 현재고) 집계
     * 예: GET /materials/plan?productCode=TIRE001&fromDate=2025-09-01&toDate=2025-09-30
     * 모든 파라미터는 선택 사항
     */
    @GetMapping("/plan")
    public List<MaterialPlanModel> getMaterialPlan(
            @RequestParam(required = false) String productCode,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ) {
        return materialPlanService.getMaterialPlan(productCode, fromDate, toDate);
    }
}
