// 2. Mapper Interface - MachineMapper.java
package com.example.demo.mapper;

import com.example.demo.model.Machine;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MachineMapper {
    Machine selectMachineByName(@Param("machineName") String machineName);
}
