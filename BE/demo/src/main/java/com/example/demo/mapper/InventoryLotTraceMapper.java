package com.example.demo.mapper;


import com.example.demo.model.InventoryLotTraceModel;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface InventoryLotTraceMapper {
    List<InventoryLotTraceModel> findLotsByPartCode(String partCode);
}
