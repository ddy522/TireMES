package com.example.demo.service;

import com.example.demo.mapper.WorkSheetMapper;
import com.example.demo.model.WorkSheetModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class WorkSheetService {
    private final WorkSheetMapper workSheetMapper;

    public List<WorkSheetModel> getAll() {
        return workSheetMapper.findAllWorkList();
    }

    public List<WorkSheetModel> getByProcessName(String processNameEng) {
        return workSheetMapper.findByProcessNameEng(processNameEng);
    }
}
