package com.example.demo.controller;

import com.example.demo.model.WorkSheetModel;
import com.example.demo.service.WorkSheetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/worksheet") // 권장: 루트 대신 명확한 베이스 경로
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173") // Vite dev 서버 주소
public class WorkSheetController {
    private final WorkSheetService service;

    @GetMapping("/all")
    public List<WorkSheetModel> getAllWorkSheets() {
        return service.getAll();
    }

    @GetMapping("/{processNameEng}")
    public List<WorkSheetModel> getByProcess(@PathVariable("processNameEng") String processNameEng) {
        return service.getByProcessName(processNameEng);
    }

    @GetMapping("/search")
    public List<WorkSheetModel> search(
            @RequestParam(required = false) String partCode,
            @RequestParam(required = false) String workStatus,
            @RequestParam(required = false) String workNo,
            @RequestParam(required = false) String processNameEng
    ) {
        return service.search(partCode, workStatus, workNo, processNameEng);
    }

}
