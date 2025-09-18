package com.example.demo.controller;

import com.example.demo.service.CommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/common")
public class CommonSelectController {
    private final CommonService commonService;

    public CommonSelectController(CommonService commonService) {
        this.commonService = commonService;
    }

    @GetMapping("/select")
    public List<Map<String, Object>> getSelectOptions(@RequestParam String type){
        return  commonService.getSelectOption(type);
    }
}
