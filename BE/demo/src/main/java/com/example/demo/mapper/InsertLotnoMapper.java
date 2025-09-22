package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InsertLotnoMapper {

    // 1. LOT의 다음 공정 조회
    String getNextProcess(@Param("lotNo") String lotNo);

    // 2. LOT와 작업지시 연결 여부 확인
    boolean isLotLinkedToWorkOrder(@Param("lotNo") String lotNo);

    // 3. 동일 work_no + process_code 존재 여부 확인
    boolean existsWorkNoProcess(@Param("lotNo") String lotNo, @Param("proc") String proc);

    // 4. lot 참조하여 작업지시 insert
    void insertLotProcedure(@Param("lotno") String lotno, @Param("proc") String proc);
}
