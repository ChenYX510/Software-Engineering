package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.domain.SimulationcityRecord;

public interface SimulationRecordMapper {
    List<Long> selectIdsByCity(String city);
    List<SimulationcityRecord> selectSimulationRecordsByCity( String city);
    List<SimulationcityRecord> selectSimulationLockRecordsByCity( String city);
    List<SimulationcityRecord> selectSimulationMADDPGRecordsByCity( String city);
}
