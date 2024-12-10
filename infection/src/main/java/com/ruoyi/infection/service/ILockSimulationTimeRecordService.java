package com.ruoyi.infection.service;
import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import java.util.Map;
import java.util.List;
public interface ILockSimulationTimeRecordService {
    void addLockSimulationTimeRecord(String city, String startTime,String userId);
    Map<String, Object> getLockAndMaddpgSimulationTime(Map<String, String> requestBody);
    Map<String, Object> getShpJson(String city,String userId);
    Map<String, Object> getPolicyResult(String city, int policyDay, int policyTime, String simulationFileName,String userId);
    Map<String, Object> getMADDPGSimulationResult(String city, int date, int time, String simulationFileName,String userId);
}