package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/lockSimulation")
public class LockSimulationTimeRecordController {

    @Autowired
    private ILockSimulationTimeRecordService lockSimulationTimeRecordService;

    @PostMapping("/get_lock_simulation_start_time")
    public String getLockSimulationStartTime(@RequestBody Map<String, String> requestBody) {
        String city = requestBody.get("city");
        String startTime = requestBody.get("start_time");
        String userId = requestBody.get("userId");
        lockSimulationTimeRecordService.addLockSimulationTimeRecord(city, startTime,userId);
        return "{\"msg\": \"success\"}";
    }
    @PostMapping("/get_lock_and_maddpg_simulation_time")
    public AjaxResult getLockAndMaddpgSimulationTime(@RequestBody Map<String, String> requestBody) {
        try {
            Map<String, Object> result = lockSimulationTimeRecordService.getLockAndMaddpgSimulationTime(requestBody);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("获取模拟时间失败：" + e.getMessage());
        }
    }
}