// 3. Service - MachineService.java
package com.example.demo.service;

import com.example.demo.mapper.MachineMapper;
import com.example.demo.model.Machine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MachineService {

    @Autowired
    private MachineMapper machineMapper;

    public Machine getMachineByName(String machineName) {
        return machineMapper.selectMachineByName(machineName);
    }
}
