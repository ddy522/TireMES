package com.example.demo.service;

import com.example.demo.mapper.MaterialPlanMapper;
import com.example.demo.model.MaterialPlanModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialPlanService {

    private final MaterialPlanMapper materialPlanMapper;

    public List<MaterialPlanModel> getMaterialPlan(String productCode, String fromDate, String toDate) {
        return materialPlanMapper.selectMaterialPlan(productCode, fromDate, toDate);
    }
}
