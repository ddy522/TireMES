package com.example.demo.controller;

import com.example.demo.model.InventoryLotTraceModel;
import com.example.demo.model.InventoryModel;
import com.example.demo.service.InventoryLotTraceService;
import com.example.demo.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vite dev 서버 주소
public class InventoryController {
    private final InventoryService service;

    @GetMapping("/all")
    public List<InventoryModel> getAllInventory() { return service.getAll();}

    @GetMapping
    public List<InventoryModel> getByCode(@RequestParam(value = "code", required = false) String code) {
        if (code == null || code.isBlank()) {
            return service.getAll();
        } else {
            return service.findByPartCodeLike(code);
        }
    }

    private final InventoryLotTraceService inventoryLotTraceService;

    @GetMapping("/inventory/{partCode}/lots")
    public List<InventoryLotTraceModel> getLotsByPartCode(@PathVariable String partCode) {
        return inventoryLotTraceService.getLotsByPartCode(partCode);
    }


}
