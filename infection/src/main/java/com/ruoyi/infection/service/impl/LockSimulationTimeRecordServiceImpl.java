package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import com.ruoyi.infection.mapper.LockSimulationTimeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
@Service
public class LockSimulationTimeRecordServiceImpl implements ILockSimulationTimeRecordService {

    @Autowired
    private LockSimulationTimeRecordMapper lockSimulationTimeRecordMapper;

    @Override
    public void addLockSimulationTimeRecord(String city, String startTime) {
        Integer maxId = lockSimulationTimeRecordMapper.selectMaxId();
        int newId = (maxId == null) ? 0 : maxId + 1;
        LockSimulationTimeRecord record = new LockSimulationTimeRecord(newId, startTime, "NULL", "NULL", city, "False");
        lockSimulationTimeRecordMapper.insertLockSimulationTimeRecord(record);
    }
}