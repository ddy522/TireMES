package com.example.demo.controller;


import com.example.demo.mapper.InsertLotnoMapper;
import com.example.demo.service.InsertLotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bom")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class InsertBomController {

    private final InsertLotService insertLotService;

    @PostMapping("/bomInsert")
    public Map<String,Object> materialInsertLot(@RequestBody Map<String, Object> param){
        Map<String, Object> result = new HashMap<>();

        String lotno = (String) param.get("lotno");
        String proc = (String) param.get("proc");
        String worksheet_skey = (String) param.get("worksheetSkey");

        try {
            insertLotService.materialInsertLot(lotno, proc, worksheet_skey);
            result.put("success", true);
            result.put("message", "자재 LOT 등록 성공!");
        } catch(RuntimeException e){
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;

    }

    @GetMapping("/lotList/{worksheetId}")
    public List<InsertLotnoMapper> getLotList(
            @PathVariable("worksheetId") String worksheetId,       // URL 경로 변수
            @RequestParam("worksheetSkey") String worksheetSkey    // 쿼리 파라미터
    ) {
        System.out.println("worksheetId: " + worksheetId);
        System.out.println("worksheetSkey: " + worksheetSkey);

        // 예: service에서 조회
        return insertLotService.getInputLots(worksheetId, worksheetSkey);
    }


    // DELETE 요청 처리
    @DeleteMapping("/deleteLot")
    public ResponseEntity<String> deleteLot(
            @RequestParam String lotNo,
            @RequestParam String worksheetSkey
    ) {
        boolean success = insertLotService.deleteLot(lotNo, worksheetSkey);
        if (success) {
            return ResponseEntity.ok("LOT 삭제 성공");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("LOT 삭제 실패");
        }
    }
}
