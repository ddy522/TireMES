package com.example.demo.controller;


import com.example.demo.service.InsertLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/worksheet")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class InsertLotController {
    private final InsertLotService insertLotService;

    //Lotno 추가
    @PostMapping("/insertLot")
    public Map<String,Object> insertLot(@RequestBody Map<String, Object> param){
        Map<String, Object> result = new HashMap<>();

        String lotno = (String) param.get("lotNo");
        String proc = (String) param.get("proc");

        try {
            insertLotService.insertLot(lotno, proc);
            result.put("success", true);
            result.put("message", "LOT 등록 성공!");
        } catch(RuntimeException e){
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;

    }

    @PostMapping("/insertProduction")
    public Map<String,Object> insertProduction(@RequestBody Map<String, Object> param){
        Map<String, Object> result = new HashMap<>();

        int doneQty = (int) param.get("doneQty");
        String remark = (String) param.get("remark");
        String worksheetSkey = (String) param.get("worksheetSkey");

        try {
            insertLotService.insertProduction(doneQty, worksheetSkey, remark);
            result.put("success", true);
            result.put("message", "생산 LOT 등록 성공!");
        } catch(RuntimeException e){
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}
