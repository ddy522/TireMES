package com.example.demo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommonMapper {
    // 이지윤
    List<Map<String,Object>> getSelectOptions(@Param("type") String type);
}
