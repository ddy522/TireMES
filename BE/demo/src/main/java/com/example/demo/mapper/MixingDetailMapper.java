package com.example.demo.mapper;

import com.example.demo.model.MixingDetailWorksheet;
import com.example.demo.model.MixingDetailBom;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MixingDetailMapper {

    /**
     * 작업지시서 상세정보 조회
     * @param workNo 작업번호
     * @return 작업지시서 상세정보
     */
    MixingDetailWorksheet selectWorksheetDetails(@Param("workNo") String workNo);

    /**
     * 작업번호로 BOM 상세정보 조회
     * @param workNo 작업번호
     * @return BOM 상세정보 리스트
     */
    List<MixingDetailBom> selectBomDetailsByWorkNo(@Param("workNo") String workNo);
}