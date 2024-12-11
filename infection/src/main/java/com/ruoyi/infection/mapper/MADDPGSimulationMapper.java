package com.ruoyi.infection.mapper;

import org.apache.ibatis.annotations.Param;

public interface MADDPGSimulationMapper {
    // 获取最新的决策id
    Integer getLatestSimulationId(@Param("userId") long userId);

    // 根据无封控模拟的id获取强化学习策略文件名称
    String getPolicyFileNameBySimulationId(@Param("userId") long userId, @Param("unlockSimulationId") int unlockSimulationId);

    String getQueryFileNameBySimulationId(@Param("userId") long userId, @Param("unlockSimulationId") int unlockSimulationId);

    Integer getSimulationIdByFilePath(@Param("userId") long userId, @Param("simulationFileName") String simulationFileName);
}
