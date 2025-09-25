package com.example.demo.controller;

import com.example.demo.model.MixingDetailWorksheet;
import com.example.demo.model.MixingDetailBom;
import com.example.demo.service.MixingDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

/**
 * 믹싱 상세정보 컨트롤러
 */
@RestController
@RequestMapping("/api/mixing-detail")
@CrossOrigin(origins = "http://localhost:5173") // Vue 개발 서버 CORS 허용
public class MixingDetailController {

    private final MixingDetailService mixingDetailService;

    @Autowired
    public MixingDetailController(MixingDetailService mixingDetailService) {
        this.mixingDetailService = mixingDetailService;
    }

    /**
     * 작업지시서 상세정보 조회
     * @param workNo 작업번호
     * @return 작업지시서 상세정보
     */
    @GetMapping("/worksheet/{workNo}")
    public ResponseEntity<MixingDetailWorksheet> getWorksheetDetails(
            @PathVariable String workNo, @RequestParam String process) {

        MixingDetailWorksheet worksheet = mixingDetailService.getWorksheetDetails(workNo,process);

        if (worksheet != null) {
            return ResponseEntity.ok(worksheet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/test")
    public String test() {
        return "API 테스트 성공!";
    }

    /**
     * 작업번호로 BOM 상세정보 조회
     * @param workNo 작업번호
     * @return BOM 상세정보 리스트
     */
    @GetMapping("/bom/{workNo}")
    public ResponseEntity<List<MixingDetailBom>> getBomDetailsByWorkNo(
            @PathVariable String workNo) {

        System.out.println("BOM API 호출됨 - workNo: " + workNo);

        try {
            List<MixingDetailBom> bomList = mixingDetailService.getBomDetailsByWorkNo(workNo);

            System.out.println("BOM 데이터 조회 결과: " + bomList);
            System.out.println("BOM 리스트 크기: " + bomList.size());

            if (!bomList.isEmpty()) {
                return ResponseEntity.ok(bomList);
            } else {
                System.out.println("BOM 데이터가 비어있음 - 204 응답");
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            System.out.println("BOM 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 작업번호의 전체 정보 조회 (작업지시서 + BOM)
     * @param workNo 작업번호
     * @return 전체 정보
     */
    @GetMapping("/complete/{workNo}")
    public ResponseEntity<MixingDetailResponse> getCompleteDetails(
            @PathVariable String workNo) {

        MixingDetailWorksheet worksheet = mixingDetailService.getWorksheetDetails(workNo,"02");
        List<MixingDetailBom> bomList = mixingDetailService.getBomDetailsByWorkNo(workNo);

        if (worksheet != null) {
            MixingDetailResponse response = new MixingDetailResponse(worksheet, bomList);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 전체 정보 응답 DTO
     */
    public static class MixingDetailResponse {
        private MixingDetailWorksheet worksheet;
        private List<MixingDetailBom> bomList;

        public MixingDetailResponse(MixingDetailWorksheet worksheet, List<MixingDetailBom> bomList) {
            this.worksheet = worksheet;
            this.bomList = bomList;
        }

        // getters and setters
        public MixingDetailWorksheet getWorksheet() { return worksheet; }
        public void setWorksheet(MixingDetailWorksheet worksheet) { this.worksheet = worksheet; }
        public List<MixingDetailBom> getBomList() { return bomList; }
        public void setBomList(List<MixingDetailBom> bomList) { this.bomList = bomList; }
    }
}