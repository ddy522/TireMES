package com.example.demo.service;

import com.example.demo.mapper.AlarmMapper;
import com.example.demo.model.AlarmModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlarmService {
    @Autowired
    private AlarmMapper alarmMapper;

    public List<AlarmModel> getAlarms() {
        return alarmMapper.getAlarms(); // 매퍼에서 바로 조회
    }

}
