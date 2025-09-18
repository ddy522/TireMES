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
}
