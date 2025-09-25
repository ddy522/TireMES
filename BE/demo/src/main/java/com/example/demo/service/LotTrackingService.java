package com.example.demo.service;


import com.example.demo.mapper.LotTrackingMapper;
import com.example.demo.model.LotTrackingModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LotTrackingService {
    private final LotTrackingMapper lotTrackingMapper;

    public List<LotTrackingModel> getLotTrackingByLotNo(String lotNo) {
        System.out.println("serviceLotno : " + lotNo);
        return lotTrackingMapper.selectLotTracking(lotNo);
    }

}
