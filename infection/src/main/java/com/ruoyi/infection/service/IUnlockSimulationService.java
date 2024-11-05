package com.ruoyi.infection.service;
import com.ruoyi.infection.domain.UnlockSimulation;
import java.util.Map;
import java.util.List;

public interface IUnlockSimulationService {
    List<UnlockSimulation> getSimulationResultsByUserId(Long userId);
    String inquireCitySimulationResult(Long userId);
}

