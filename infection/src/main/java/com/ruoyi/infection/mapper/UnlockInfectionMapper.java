package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.infection.domain.UnlockSimulation;

public interface UnlockInfectionMapper {
    List<UnlockSimulation> selectSimulationResultsByUserId(@Param("userId") Long userId);
}
