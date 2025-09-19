package com.example.demo.service;

import com.example.demo.mapper.InsertLotnoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
