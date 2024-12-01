package com.ruoyi.infection.service;
import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.domain.SimulationRecord;
import java.util.Map;
import com.ruoyi.infection.domain.SimulationcityRecord;
import java.util.List;


public interface ISimulationRecordService {
    List<Long> getIdsByCity(String city);
    List<CitySimulationResult> getCitySimulationResults(String userId);
    List<CitySimulationResult> getCitySimulationLockResults(String userId);
    List<CitySimulationResult> getCitySimulationMADDPGResults(String userId);
}
