package com.ruoyi.infection.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import com.ruoyi.infection.domain.SimulationRecord;
public interface LockSimulationTimeRecordMapper {
    Integer selectMaxId();
    void insertLockSimulationTimeRecord(LockSimulationTimeRecord record);
}