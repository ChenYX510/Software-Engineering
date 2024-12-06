package com.ruoyi.infection.service;
import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.domain.SimulationRecord;
import java.util.Map;
import com.ruoyi.infection.domain.SimulationcityRecord;
import java.util.List;


public interface ISimulationRecordService {
    List<Long> getIdsByCity(String city,String userId);
    List<CitySimulationResult> getCitySimulationResults(String userId);
    List<CitySimulationResult> getCitySimulationLockResults(String userId);
    List<CitySimulationResult> getCitySimulationMADDPGResults(String userId);
    Map<String, Object> getSimulationResult(String city, int simulationDay, int simulationHour, String simulationFileName,String userId);
    Map<String, Object> getLockSimulationResult(String city, int simulationDay, int simulationHour, String simulationFileName,String userId);
    Map<String, Object> getSimulationRiskPoints(String city, int simulationDay, int simulationHour, int thresholdInfected, String simulationFileName,String userId);
    Map<String, Object> getLockSimulationRiskPoints(String city, int simulationDay, int simulationHour, int thresholdInfected, String simulationFileName,String userId);
    Map<String, Object> getgrid_control_policy(String city,String userId);
    Map<String, Object> getCity4LevelName(String city,String userId);
}
