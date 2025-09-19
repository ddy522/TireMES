package com.example.demo.service;

import com.example.demo.mapper.MixingDetailMapper;
import com.example.demo.model.MixingDetailWorksheet;
import com.example.demo.model.MixingDetailBom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 믹싱 상세정보 서비스
 */
@Service
public class MixingDetailService {

    private final MixingDetailMapper mixingDetailMapper;

    @Autowired
    public MixingDetailService(MixingDetailMapper mixingDetailMapper) {
        this.mixingDetailMapper = mixingDetailMapper;
    }

    /**
     * 작업지시서 상세정보 조회
     * @param workNo 작업번호
     * @return 작업지시서 상세정보
     */
    public MixingDetailWorksheet getWorksheetDetails(String workNo) {
        return mixingDetailMapper.selectWorksheetDetails(workNo);
    }

    /**
     * 작업번호로 BOM 상세정보 조회
     * @param workNo 작업번호
     * @return BOM 상세정보 리스트
     */
    public List<MixingDetailBom> getBomDetailsByWorkNo(String workNo) {
        return mixingDetailMapper.selectBomDetailsByWorkNo(workNo);
    }
}
