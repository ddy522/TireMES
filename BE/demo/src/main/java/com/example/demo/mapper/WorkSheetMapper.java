package com.example.demo.mapper;

import com.example.demo.model.WorkSheetModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkSheetMapper {

    // 전체 작업지시 조회
    List<WorkSheetModel> findAllWorkList();

    // 공정명(영문)으로 조회
    List<WorkSheetModel> findByProcessNameEng(@Param("processNameEng") String processNameEng);

    // 선택 조건 검색 (셀렉트 박스)
    List<WorkSheetModel> searchWorkSheets(
            @Param("partCode") String partCode,
            @Param("workStatus") String workStatus,
            @Param("workNo") String workNo,
            @Param("processNameEng") String processNameEng
    );
}
