package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import com.ruoyi.infection.domain.SimulationRequest;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/get_grid_shp")
    public Map<String, Object> getGridShp(@RequestParam String city,@RequestParam String userId) {
        return lockSimulationTimeRecordService.getShpJson(city,userId);
    }
    @PostMapping("/get_policy_result")
    public Map<String, Object> getPolicyResult(@RequestBody Map<String, Object> requestBody) {
        String city = (String)requestBody.get("city");
        int policyDay = (int)requestBody.get("policyday");
        int policyTime= (int)requestBody.get("policytime");
        String userId = (String)requestBody.get("userId");
        String simulationFileName = requestBody.containsKey("simulation_file_name") ? (String) requestBody.get("simulation_file_name") : "latestRecord";
        return lockSimulationTimeRecordService.getPolicyResult(city, policyDay, policyTime, simulationFileName,userId);
    }
        @PostMapping("/get_MADDPG_simulation_result")
    public ResponseEntity<Map<String, Object>> getMADDPGSimulationResult(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        String userId = (String) requestBody.get("user_id");
        int date = (int) requestBody.get("date");
        int time = (int) requestBody.get("time");
        String simulationFileName = requestBody.containsKey("simulation_file_name")
                ? (String) requestBody.get("simulation_file_name")
                : "latestRecord";

        Map<String, Object> response = lockSimulationTimeRecordService.getMADDPGSimulationResult(city, date, time, simulationFileName,userId);
        return ResponseEntity.ok(response);
    }
}