package com.example.demo.service;

import com.example.demo.mapper.LotListMapper;
import com.example.demo.model.LotListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LotListService {
    @Autowired
    private LotListMapper lotListMapper;

    // LOT 목록 조회
    public List<LotListModel> getLots(String worksheetSkey) {
        return lotListMapper.selectLotsByWorksheetSkey(worksheetSkey);
    }

    // LOT 삭제
    @Transactional
    public void deleteLot(String lotNo, String worksheetSkey) {
        lotListMapper.deleteLot(lotNo, worksheetSkey);
    }
}
