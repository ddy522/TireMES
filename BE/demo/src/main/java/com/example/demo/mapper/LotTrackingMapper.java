package com.example.demo.mapper;


import com.example.demo.model.LotTrackingModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LotTrackingMapper {
    List<LotTrackingModel> selectLotTracking(@Param("lotNo") String lotNo);
}
