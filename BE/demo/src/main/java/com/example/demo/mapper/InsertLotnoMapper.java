package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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


    // 자재 투입 로직 시작
    String getProcessNo(@Param("lotno") String lotNo);                   // lot의 현재 process_no 조회
    boolean isLotInBom(@Param("lotno")  String lotNo , @Param("worksheet_skey")  String worksheet_skey);                   // BOM에 포함 여부
    boolean isLotAlreadyInserted(@Param("lotno")  String lotNo , @Param("worksheet_skey")  String worksheet_skey);  // 이미 투입된 Lot인지
    void materialInsertLot(@Param("lotno") String lotNo, @Param("worksheet_skey") String worksheet_skey, @Param("proc") String proc); // MySQL 프로시저 호출


    // 투입 자재 조회
    List<InsertLotnoMapper> selectInputLots(
            @Param("worksheetId") String worksheetId,
            @Param("worksheetSkey") String worksheetSkey
    );

    int deleteLot(@Param("lotNo") String lotNo, @Param("worksheetSkey") String worksheetSkey);

    void insertProduction(@Param("doneQty") int doneQty,
                          @Param("worksheetSkey") String worksheetSkey,
                          @Param("remark") String remark);

    void insertInspectionProduction(@Param("workNo") String workNo,
                          @Param("decision") int decision);




}
