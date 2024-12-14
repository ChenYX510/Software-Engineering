package com.ruoyi.web.controller.infection;
import com.ruoyi.infection.domain.SimulationcityRecord;
import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.service.ISimulationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

@RestController
@RequestMapping("/api/simulation")
public class SimulationRecordController {
    private static final String ROOT_FILE = System.getProperty("user.dir") + "\\testUser\\";
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
    @PostMapping("/get_DSIHR")
    public Map<String, Object> getDSIHR(@RequestParam("file") MultipartFile file,
                                        @RequestParam("I_H_para") double I_H_para,
                                        @RequestParam("I_R_para") double I_R_para,
                                        @RequestParam("H_R_para") double H_R_para,
                                        @RequestParam("userId") String userId) {
                                            Map<String, Object> response = new HashMap<>();

                                            // 检查文件是否为空
                                            if (file.isEmpty()) {
                                                response.put("message", "文件上传失败");
                                                return response;
                                            }
        String uploadDir = ROOT_FILE+userId+"\\";  // 自定义目录作为示例
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            response.put("message", "文件上传失败，文件路径为空");
            return response;
        }

        String filepath = null;  // 在 try 块外部声明 filepath
                try {
            // 创建带有唯一名称的目标文件
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
String strCurrentTimeMillis = String.valueOf(System.currentTimeMillis());
            File targetFile = new File(dir+strCurrentTimeMillis + "." + fileExtension);
        filepath=uploadDir+strCurrentTimeMillis+ "." + fileExtension;
            // 将上传的文件保存到目标文件
            file.transferTo(targetFile);
            // 文件上传成功后，记录文件路径
            response.put("message", "文件上传成功");
            response.put("filePath", targetFile.getAbsolutePath());


        } catch (IOException e) {
            e.printStackTrace();
            response.put("message", "文件保存失败：" + e.getMessage());
        }
        if (filepath != null) {
            return simulationRecordService.getDSIHR(filepath, I_H_para, I_R_para, H_R_para, userId);
        } else {
            response.put("message", "文件路径未设置");
            return response;
        }
    }
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return ""; // No extension
        }
        return fileName.substring(lastIndexOfDot + 1).toLowerCase();
    }
}
