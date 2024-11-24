package com.ruoyi.infection.mapper;

import org.apache.ibatis.annotations.Param;

public interface SimulationTaskMapper {
    // 获取 user_infection_simulation_result 表中某用户指定列的最大值
    Integer getMaxResultId(@Param("userId") long userId,
                           @Param("resultTable") String resultTable,
                           @Param("resultColumn") String resultColumn);

    // 在指定的结果表中插入新记录
    int insertSimulationResult(@Param("resultTable") String resultTable,
                               @Param("userId") long userId,
                               @Param("resultId") int resultId,
                               @Param("filepath") String filepath,
                               @Param("cityName") String cityName,
                               @Param("state") String state);

    // 更新指定结果表中记录的状态
    int updateTaskStatus(@Param("resultTable") String resultTable,
                         @Param("resultColumn") String resultColumn,
                         @Param("state") String state,
                         @Param("filepath") String filepath,
                         @Param("resultId") int resultId);
}
