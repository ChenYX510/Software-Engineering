package com.ruoyi.infection.mapper;
import java.util.List;
import java.util.Map;
import com.ruoyi.infection.domain.UnlockSimulation;

public interface UnlockInfectionMapper {
    List<UnlockSimulation> getSimulationRecords(String city);
}
