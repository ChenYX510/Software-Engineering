package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import com.ruoyi.infection.mapper.LockSimulationTimeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
@Service
public class LockSimulationTimeRecordServiceImpl implements ILockSimulationTimeRecordService {

    @Autowired
    private LockSimulationTimeRecordMapper lockSimulationTimeRecordMapper;

    @Override
    public void addLockSimulationTimeRecord(String city, String startTime,String userId) {
        Integer maxId = lockSimulationTimeRecordMapper.selectMaxId(userId);
        int newId = (maxId == null) ? 0 : maxId + 1;
        LockSimulationTimeRecord record = new LockSimulationTimeRecord(newId, startTime, "NULL", "NULL", city, "False",userId);
        lockSimulationTimeRecordMapper.insertLockSimulationTimeRecord(record);
    }
     @Override
    public Map<String, Object> getLockAndMaddpgSimulationTime(Map<String, String> requestBody) {
        String simulationCity = requestBody.get("simulation_city");
        String userId = requestBody.get("user_id");
        Map<String, Object> result = new HashMap<>();

        // 初始化变量
        String lockSettingStartTime = "NULL";
        String lockSettingEndTime = "NULL";
        String lockSimulationStartTime = "NULL";
        String lockSimulationEndTime = "NULL";

        String maddpgPolicyStartTime = "NULL";
        String maddpgPolicyEndTime = "NULL";
        String maddpgSimulationStartTime = "NULL";
        String maddpgSimulationEndTime = "NULL";

        // 查询逻辑实现（根据 requestBody 的 key 判断）
        if (requestBody.containsKey("lock_simulation_time")) {
            String simulationTime = requestBody.get("lock_simulation_time");
            Map<String, String> lockData = lockSimulationTimeRecordMapper.getLockSimulationTimeByTime(simulationTime,userId);
            if (lockData != null) {
                lockSettingStartTime = lockData.get("lock_region_start_time");
                lockSettingEndTime = lockData.get("lock_region_end_time");
                lockSimulationStartTime = lockData.get("lock_region_end_time");
                lockSimulationEndTime = lockData.get("simulation_end_time");
            }
        } else {
            // 获取最新的锁定模拟记录
            Map<String, String> lockData = lockSimulationTimeRecordMapper.getLatestLockSimulationTime(simulationCity,userId);
            if (lockData != null) {
                lockSettingStartTime = lockData.get("lock_region_start_time");
                lockSettingEndTime = lockData.get("lock_region_end_time");
                lockSimulationStartTime = lockData.get("lock_region_end_time");
                lockSimulationEndTime = lockData.get("simulation_end_time");
            } else {
                throw new RuntimeException("没有最新的手动封控模拟记录");
            }
        }

        // 添加 MADDPG 模拟的逻辑
        if (requestBody.containsKey("MADDPG_simulation_time")) {
            String maddpgTime = requestBody.get("MADDPG_simulation_time");
            Map<String, String> maddpgData = lockSimulationTimeRecordMapper.getMaddpgSimulationTimeByTime(maddpgTime,userId);
            if (maddpgData != null) {
                maddpgPolicyStartTime = maddpgData.get("maddpg_start_time");
                maddpgPolicyEndTime = maddpgData.get("maddpg_end_time");
                maddpgSimulationStartTime = maddpgData.get("simulation_start_time");
                maddpgSimulationEndTime = maddpgData.get("simulation_end_time");
            }
        } else {
            // 获取最新的自动封控模拟记录
            Map<String, String> maddpgData = lockSimulationTimeRecordMapper.getLatestMaddpgSimulationTime(simulationCity,userId);
            if (maddpgData != null) {
                maddpgPolicyStartTime = maddpgData.get("maddpg_start_time");
                maddpgPolicyEndTime = maddpgData.get("maddpg_end_time");
                maddpgSimulationStartTime = maddpgData.get("simulation_start_time");
                maddpgSimulationEndTime = maddpgData.get("simulation_end_time");
            } else {
                throw new RuntimeException("没有最新的自动封控模拟记录");
            }
        }

        // 计算时间差
        long lockSettingPeriod = calculatePeriod(lockSettingStartTime, lockSettingEndTime);
        long lockSimulationPeriod = calculatePeriod(lockSimulationStartTime, lockSimulationEndTime);
        long maddpgPolicyPeriod = calculatePeriod(maddpgPolicyStartTime, maddpgPolicyEndTime);
        long maddpgSimulationPeriod = calculatePeriod(maddpgSimulationStartTime, maddpgSimulationEndTime);

        // 返回结果
        result.put("lock_setting_start_time", lockSettingStartTime);
        result.put("lock_setting_end_time", lockSettingEndTime);
        result.put("lock_simulation_start_time", lockSimulationStartTime);
        result.put("lock_simulation_end_time", lockSimulationEndTime);
        result.put("maddpg_policy_start_time", maddpgPolicyStartTime);
        result.put("maddpg_policy_end_time", maddpgPolicyEndTime);
        result.put("maddpg_simulation_start_time", maddpgSimulationStartTime);
        result.put("maddpg_simulation_end_time", maddpgSimulationEndTime);
        result.put("lock_setting_time", lockSettingPeriod);
        result.put("lock_simulation_time", lockSimulationPeriod);
        result.put("maddpg_policy_time", maddpgPolicyPeriod);
        result.put("maddpg_simulation_time", maddpgSimulationPeriod);

        return result;
    }

    private long calculatePeriod(String startTime, String endTime) {
        if ("NULL".equals(startTime) || "NULL".equals(endTime)) {
            return 0;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss");
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            return (endDate.getTime() - startDate.getTime()) / 1000; // 返回秒
        } catch (Exception e) {
            throw new RuntimeException("时间解析失败：" + e.getMessage());
        }
    }
}