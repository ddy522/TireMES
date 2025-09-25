package com.example.demo.service;

import com.example.demo.mapper.InventoryMapper;
import com.example.demo.model.InventoryModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryMapper inventoryMapper;

    public List<InventoryModel> getAll() {
        return inventoryMapper.findAllInventory();
    }

    // 코드 "정확히" 검색
    public List<InventoryModel> findByPartCode(String partCode) {
        return inventoryMapper.findByPartCode(partCode);
    }

    // 코드 "포함" 검색
    public List<InventoryModel> findByPartCodeLike(String partCode) {
        return inventoryMapper.findByPartCodeLike(partCode);
    }
}

