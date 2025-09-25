package com.example.demo.controller;

import com.example.demo.model.LotListModel;
import com.example.demo.service.LotListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // 필요 시 CORS
@RequestMapping("/api/lot")
public class LotListController {
    @Autowired
    private LotListService lotListService;

    // LOT 목록 조회
    @GetMapping("/lotList/{worksheetSkey}")
    public List<LotListModel> getLotList(@PathVariable String worksheetSkey) {
        return lotListService.getLots(worksheetSkey);
    }

    // LOT 삭제
    @DeleteMapping("/deleteLot")
    public void deleteLot(@RequestParam String lotNo, @RequestParam String worksheetSkey) {
        lotListService.deleteLot(lotNo, worksheetSkey);
    }

}
