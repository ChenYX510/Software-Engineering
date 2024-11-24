package com.ruoyi.infection.mapper;

import org.apache.ibatis.annotations.Param;

public interface SimulationTaskMapper {
    /**
     * 获取表中当前最大 ID
     * @param tableName 表名
     * @return 当前最大 ID
     */
    Integer getMaxId(@Param("tableName") String tableName);

    /**
     * 新增模拟记录
     * @param tableName 表名
     * @param id 主键 ID
     * @param filepath 文件路径
     * @param city 城市名称
     * @param state 状态
     * @return 插入行数
     */
    int insertSimulationRecord(@Param("tableName") String tableName,
                               @Param("id") Integer id,
                               @Param("filepath") String filepath,
                               @Param("city") String city,
                               @Param("state") String state);
    /**
     * 更新任务状态
     * @param tableName 表名
     * @param state 状态值
     * @param dirName 文件路径
     * @return 更新行数
     */
    int updateTaskStatus(@Param("tableName") String tableName,
                         @Param("state") String state,
                         @Param("dirName") String dirName);
}
