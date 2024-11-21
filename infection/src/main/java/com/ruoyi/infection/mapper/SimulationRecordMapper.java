package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.domain.SimulationRecord;

public interface SimulationRecordMapper {
    List<Long> selectIdsByCity(String city);
}
