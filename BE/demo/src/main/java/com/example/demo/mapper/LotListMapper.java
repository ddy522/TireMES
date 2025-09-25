package com.example.demo.mapper;

import com.example.demo.model.LotListModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LotListMapper {
    // 특정 worksheetSkey에 대한 LOT 조회
    List<LotListModel> selectLotsByWorksheetSkey(@Param("worksheetSkey") String worksheetSkey);

    // LOT 삭제
    void deleteLot(@Param("lotNo") String lotNo, @Param("worksheetSkey") String worksheetSkey);
}
