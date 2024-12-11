package com.ruoyi.infection.service;
import  com.ruoyi.infection.domain.SimulationTask;

import java.util.Map;

public interface ISimulationTaskService {
    Map<String, Object> unlockSimulationTask(SimulationTask request);
    Map<String, Object> lockSimulationTask(SimulationTask request);
    Map<String, Object> MADDPGSimulationTask(SimulationTask request);
}

