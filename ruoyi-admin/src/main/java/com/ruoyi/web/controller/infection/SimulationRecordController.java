package com.ruoyi.web.controller.infection;
import com.ruoyi.infection.domain.SimulationcityRecord;
import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.service.ISimulationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/simulation")
public class SimulationRecordController {
    @Autowired
    private ISimulationRecordService simulationRecordService;

    @PostMapping("/test_database")
    public Map<String, Object> testDatabase(@RequestParam String userId) {
        String city = "ezhou"; // 将城市硬编码为 'ezhou'
        List<Long> curId = simulationRecordService.getIdsByCity(city,userId);

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "succeed");
        response.put("msg1", curId.toString());
        response.put("msg2", curId.isEmpty() ? "No data" : curId.get(0).toString());
        response.put("msg2_1", curId.isEmpty() ? "No data" : curId.get(0).toString());
        response.put("msg3", curId.getClass().getName());
        response.put("msg4", curId.isEmpty() ? "No data" : curId.get(0).getClass().getName());
        response.put("msg4_1", curId.isEmpty() ? "No data" : curId.get(0).getClass().getName());

        return response;
    }
    @PostMapping("/inquire_city_simulation_result")
    public List<CitySimulationResult> inquireCitySimulationResult(@RequestParam String userId) {
        return simulationRecordService.getCitySimulationResults(userId);
    }
    @PostMapping("/inquire_city_simulation_lock_result")
    public List<CitySimulationResult> inquireCitySimulationLockResult(@RequestParam String userId) {
        return simulationRecordService.getCitySimulationLockResults(userId);
    }
    @PostMapping("/query_city_simulation_MADDPG_result")
    public List<CitySimulationResult> quireCitySimulationMADDPGResult(@RequestParam String userId) {
        return simulationRecordService.getCitySimulationMADDPGResults(userId);
    }
    @PostMapping("/get_simulation_result")
    public Map<String, Object> getSimulationResult(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        int simulationDay = (int) requestBody.get("simulation_day");
        int simulationHour = (int) requestBody.get("simulation_hour");
        String simulationFileName = requestBody.containsKey("simulation_file_name") ? (String) requestBody.get("simulation_file_name") : "latestRecord";
        String userId = (String) requestBody.get("user_id");
        return simulationRecordService.getSimulationResult(city, simulationDay, simulationHour, simulationFileName,userId);
    }
    @PostMapping("/get_lock_simulation_result")
    public Map<String, Object> getLockSimulationResult(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        int simulationDay = (int) requestBody.get("simulation_day");
        int simulationHour = (int) requestBody.get("simulation_hour");
        String simulationFileName = requestBody.containsKey("simulation_file_name") ? (String) requestBody.get("simulation_file_name") : "latestRecord";
        String userId = (String) requestBody.get("user_id");
        return simulationRecordService.getLockSimulationResult(city, simulationDay, simulationHour, simulationFileName,userId);
    }
    @PostMapping("/get_simulation_risk_point")
    public Map<String, Object> getSimulationRiskPoints(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        String userId = (String) requestBody.get("user_id");
        int simulationDay = (int) requestBody.get("simulation_day");
        int simulationHour = (int) requestBody.get("simulation_hour");
        int thresholdInfected = (int) requestBody.get("threshold_Infected");
        String simulationFileName = requestBody.getOrDefault("simulation_file_name", "latestRecord").toString();

        return simulationRecordService.getSimulationRiskPoints(city, simulationDay, simulationHour, thresholdInfected, simulationFileName,userId);
    }
    @PostMapping("/get_lock_simulation_risk_point")
    public Map<String, Object> getLockSimulationRiskPoints(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        String userId = (String) requestBody.get("user_id");
        int simulationDay = (int) requestBody.get("simulation_day");
        int simulationHour = (int) requestBody.get("simulation_hour");
        int thresholdInfected = (int) requestBody.get("threshold_Infected");
        String simulationFileName = requestBody.getOrDefault("simulation_file_name", "latestRecord").toString();

        return simulationRecordService.getLockSimulationRiskPoints(city, simulationDay, simulationHour, thresholdInfected, simulationFileName,userId);
    }
    @PostMapping("/get_grid_control_policy_func_finish")
    public Map<String, Object> grid_control_policy(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        String userId = (String) requestBody.get("user_id");

        return simulationRecordService.getgrid_control_policy(city,userId);
    }
    @PostMapping("/get_city_4_level_name")
    public Map<String, Object> getCity4LevelName(@RequestBody Map<String, Object> requestBody) {
        String city = (String) requestBody.get("city");
        String userId = (String) requestBody.get("user_id");

        return simulationRecordService.getCity4LevelName(city,userId);
    }
}
