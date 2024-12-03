package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.domain.SimulationcityRecord;

public interface SimulationRecordMapper {
    List<Long> selectIdsByCity(String city);
    List<SimulationcityRecord> selectSimulationRecordsByCity(@Param("city") String city,@Param("userId") String userId);
    List<SimulationcityRecord> selectSimulationLockRecordsByCity(@Param("city") String city,@Param("userId") String userId);
    List<SimulationcityRecord> selectSimulationMADDPGRecordsByCity(@Param("city") String city,@Param("userId") String userId);
}
