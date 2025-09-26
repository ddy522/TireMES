package com.example.demo.mapper;

import com.example.demo.model.AlarmModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AlarmMapper {
//    @Select("SELECT id, title, time, level FROM alarms ORDER BY time DESC")
    List<AlarmModel> getAlarms();
}
