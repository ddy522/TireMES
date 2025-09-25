package com.example.demo.service;

import com.example.demo.mapper.InsertLotnoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InsertLotService {
    @Autowired
    private InsertLotnoMapper insertLotnoMapper;

    @Transactional
    public void insertLot(String lotNo, String proc) {
        try {

            // 1~3 단계 검증 (Mapper에서 결과 받아서 필요 시 RuntimeException 발생)
            String nextProc = insertLotnoMapper.getNextProcess(lotNo);
            if (!proc.equals(nextProc)) {
                throw new RuntimeException("스캔한 LOT의 다음 공정이 현재 공정과 일치하지 않습니다.");
            }

            if (!insertLotnoMapper.isLotLinkedToWorkOrder(lotNo)) {
                throw new RuntimeException("LOT가 작업지시와 연결되어 있지 않습니다.");
            }

            if (insertLotnoMapper.existsWorkNoProcess(lotNo, proc)) {
                throw new RuntimeException("이미 동일 작업지시와 공정 데이터가 존재합니다.");
            }



            // 검증 통과 시 프로시저 호출
            insertLotnoMapper.insertLotProcedure(lotNo, proc);  // MySQL 프로시저 호출
        } catch(Exception e) {
            throw new RuntimeException("LOT 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    @Transactional
    public void materialInsertLot(String lotno, String proc, String worksheet_skey) {
        try {
            // 1. process_no가 01번인지 확인
            String currentProcess = insertLotnoMapper.getProcessNo(lotno);
            if (!"01".equals(currentProcess)) {
                throw new RuntimeException("현재 LOT는 자재 LOT가 아니므로 투입할 수 없습니다.");
            }

            // 2. BOM에 포함된 품번인지 확인
            boolean inBom = insertLotnoMapper.isLotInBom(lotno, worksheet_skey);
            if (!inBom) {
                throw new RuntimeException("LOT가 BOM에 포함되어 있지 않습니다.");
            }

            // 3. 이미 투입된 LOT인지 확인
            boolean alreadyInserted = insertLotnoMapper.isLotAlreadyInserted(lotno, worksheet_skey);
            if (alreadyInserted) {
                throw new RuntimeException("이미 투입된 LOT입니다.");
            }

            // 모든 조건 통과 시 프로시저 호출
            insertLotnoMapper.materialInsertLot(lotno, worksheet_skey, proc);

        } catch (Exception e) {
            throw new RuntimeException("LOT 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    // worksheetId와 worksheetSkey를 받아서 DB에서 조회
    public List<InsertLotnoMapper> getInputLots(String worksheetId, String worksheetSkey) {
        return insertLotnoMapper.selectInputLots(worksheetId, worksheetSkey);
    }

    public boolean deleteLot(String lotNo, String worksheetSkey) {
        int affected = insertLotnoMapper.deleteLot(lotNo, worksheetSkey);
        return affected > 0;
    }

    @Transactional
    public void insertProduction(int doneQty, String worksheetSkey, String remark) {
        try {
            // 검증 통과 시 프로시저 호출
            insertLotnoMapper.insertProduction(doneQty, worksheetSkey, remark);  // MySQL 프로시저 호출
        } catch(Exception e) {
            throw new RuntimeException("LOT 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public void insertInspectionProduction(String workNo, int decision) {
        try {
            // 검증 통과 시 프로시저 호출
            insertLotnoMapper.insertInspectionProduction(workNo, decision);  // MySQL 프로시저 호출
        } catch(Exception e) {
            throw new RuntimeException("LOT 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
    }





}
