package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InspectionMapper {
    void insertVisionWorkNos(@Param("list") List<String> workNos);
}