package com.example.demo.service;

import com.example.demo.mapper.InventoryLotTraceMapper;
import com.example.demo.model.InventoryLotTraceModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryLotTraceService {

    private final InventoryLotTraceMapper inventoryLotTraceMapper;

    public List<InventoryLotTraceModel> getLotsByPartCode(String partCode) {
        return inventoryLotTraceMapper.findLotsByPartCode(partCode);
    }
}
