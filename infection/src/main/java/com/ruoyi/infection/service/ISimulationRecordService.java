package com.ruoyi.infection.service;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.domain.SimulationRecord;
import java.util.Map;
import java.util.List;


public interface ISimulationRecordService {
    List<Long> getIdsByCity(String city);
}
