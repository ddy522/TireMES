package com.example.demo.controller;

import com.example.demo.model.LotTrackingModel;
import com.example.demo.service.LotTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/lot-tracking")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class LotTrackingController {

    private final LotTrackingService lotTrackingService;

    // LOT 하나 기준으로 관련 공정/이력 전체 반환
    @GetMapping("/{lotNo}")
    public List<LotTrackingModel> getLotTracking(@PathVariable String lotNo) {
        System.out.println("lotno:" + lotNo);
        return lotTrackingService.getLotTrackingByLotNo(lotNo);
    }
}
