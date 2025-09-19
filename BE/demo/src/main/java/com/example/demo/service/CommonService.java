package com.example.demo.service;

import com.example.demo.mapper.CommonMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonService {
    private final CommonMapper commonMapper;

    public CommonService(CommonMapper commonMapper) {
        this.commonMapper = commonMapper;
    }

    public List<Map<String, Object>> getSelectOption(String type){
        return  commonMapper.getSelectOptions(type);
    }
}
