package com.ruoyi.infection.mapper;

import org.apache.ibatis.annotations.Param;

public interface SimulationTaskMapper {
    // 获取表中某用户指定列的最大值
    Integer getMaxResultId(@Param("userId") long userId,
                           @Param("resultTable") String resultTable,
                           @Param("resultColumn") String resultColumn);

    // 在指定的结果表中插入新记录
    int insertSimulationResult(@Param("resultTable") String resultTable,
                               @Param("userId") long userId,
                               @Param("resultId") int resultId,
                               @Param("filepath") String filepath,
                               @Param("cityName") String cityName,
                               @Param("policyId") int policyId,
                               @Param("state") String state);

    // 更新指定结果表中记录的状态
    int updateTaskStatus(@Param("resultTable") String resultTable,
                         @Param("resultColumn") String resultColumn,
                         @Param("state") String state,
                         @Param("filepath") String filepath,
                         @Param("resultId") int resultId);

    /*// 查询 `maddpg_simulation_time_record` 表中最大的 ID
    Integer getMaxMADDPGRecordId(@Param("userId") long userId);

    // 插入一条新记录到 `maddpg_simulation_time_record`
    int insertMADDPGSimulationRecord(@Param("userId") long userId,
                                     @Param("id") int id, @Param("startTime") String startTime,
                                     @Param("endTime") String endTime, @Param("state") String state,
                                     @Param("city") String city, @Param("simulationEndTime") String simulationEndTime,
                                     @Param("simulationStartTime") String simulationStartTime);*/

    // 查询 `MADDPG_policy_record` 表中最大的 ID
    Integer getMaxPolicyRecordId(@Param("userId") long userId);

    // 插入一条新记录到 `MADDPG_policy_record`
    int insertPolicyRecord(@Param("id") int id, @Param("filepath") String filepath,
                           @Param("userId") long userId,
                           @Param("simulationId") Integer simulationId,
                           @Param("startTime") String startTime,
                           @Param("endTime") String endTime);

    Integer getPolicyIdByUnlockSimulationId(@Param("userId") long userId, @Param("unlockSimulationId") int unlockSimulationId);

    // 从policy表中获取当前unlock_id的最新值
    Integer getLatestSimulationIdFromPolicy(@Param("userId") long userId);

    // 根据策略的id获取强化学习策略文件名称
    String getPolicyFileNameByPolicyId(@Param("userId") long userId, @Param("policyId") int policyId);

    // 根据无封控模拟id获取无封控模拟结果的文件名称
    String getQueryFileNameBySimulationId(@Param("userId") long userId, @Param("unlockSimulationId") int unlockSimulationId);

    // 根据文件名称获取无封控模拟的id
    Integer getSimulationIdByFilePath(@Param("userId") long userId, @Param("simulationFileName") String simulationFileName);

}
