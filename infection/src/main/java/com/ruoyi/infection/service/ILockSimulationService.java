package com.ruoyi.infection.service;
import java.util.Map;

import com.ruoyi.infection.domain.SimulationRequest;

import java.util.List;

public interface ILockSimulationService {
    List<Double> getLockEveryHourInfection(String city, String userId,String simulationFileName);
    List<Double> getEveryHourInfection(String city, String userId,String simulationFileName);
    List<Double> getMADDPGEveryHourInfection(String city,String userId, String simulationFileName);
    Map<String, Object> getMADDPGRiskPoints(SimulationRequest request);
}