package com.example.demo.controller;

import com.example.demo.model.AlarmModel;
import com.example.demo.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AlarmController {
    @Autowired
    private AlarmService alarmService;

    @GetMapping("/alarms")
    public List<AlarmModel> getAlarms() {
        return alarmService.getAlarms();  // DB에서 알림 조회
    }
}
