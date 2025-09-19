package com.example.demo.mapper;

import com.example.demo.model.InventoryModel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InventoryMapper {
    List<InventoryModel> findAllInventory();
    List<InventoryModel> findByPartCode(String partCode);
    List<InventoryModel> findByPartCodeLike(String partCode);
}
