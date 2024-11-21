package com.ruoyi.infection.service;
import java.util.Map;
import java.util.List;

public interface ILockSimulationService {
    List<Double> getLockEveryHourInfection(String city, String simulationFileName);
    List<Double> getEveryHourInfection(String city, String simulationFileName);
}