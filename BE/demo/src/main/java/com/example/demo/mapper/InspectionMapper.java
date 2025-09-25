package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import com.example.demo.model.VisionDecisionModel;

@Mapper
public interface InspectionMapper {

    // ✅ vision_result에 insert
    void insertVisionWorkNos(@Param("list") List<String> workNos);

    // ✅ vision_result에서 최신 판정 조회
    VisionDecisionModel selectLatestVisionDecision(@Param("workNo") String workNo);
}