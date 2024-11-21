package com.ruoyi.infection.service;
import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import java.util.Map;
import java.util.List;
public interface ILockSimulationTimeRecordService {
    void addLockSimulationTimeRecord(String city, String startTime);
}