package com.ruoyi.infection.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.infection.domain.MADDPGSimulation;
import com.ruoyi.infection.mapper.MADDPGSimulationMapper;
import com.ruoyi.infection.service.IMADDPGSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Service
public class MADDPGSimulationServiceImpl {
    /*@Autowired
    private MADDPGSimulationMapper maddpgSimulationMapper;
    private static final String ROOT_FILE_PATH = System.getProperty("user.dir") + "\\testUser\\";

    private static final Logger logger = Logger.getLogger(SimulationTaskServiceImpl.class.getName());

    @Override
    public Map<String, Object> MADDPGSimulation(MADDPGSimulation request){
        Map<String, Object> response = new HashMap<>();

        String city = request.getSimulationCity();
        String simulationFileName = request.getSimulationFileName();
        long userId = request.getUserId();

        if (simulationFileName == null || simulationFileName.isEmpty()) {
            simulationFileName = "latestRecord"; // 默认为最新记录
        }

        Integer unlockSimulationId = null;
        String policyFileName = null;
        String queryFileName = null;

        try {
            if ("latestRecord".equals(simulationFileName)) {
                // 获取最新的模拟文件 ID
                unlockSimulationId = maddpgSimulationMapper.getLatestPolicyId(userId);

                if (unlockSimulationId == null||unlockSimulationId==0) {
                    response.put("status", false);
                    response.put("msg", "请先进行传染病模拟");
                    return response;
                }

                // 根据id获取 policy 文件名
                policyFileName = maddpgSimulationMapper.getPolicyFileNameBySimulationId(userId, unlockSimulationId);
                // 获取查询文件名
                queryFileName = maddpgSimulationMapper.getQueryFileNameBySimulationId(userId, policyId);
            } else {
                // 根据传入的文件名获取 simulation_id
                simulationFileId = maddpgSimulationMapper.getSimulationFileIdByFilePath(userId, simulationFileName);

                if (simulationFileId == -1) {
                    response.put("status", false);
                    response.put("msg", "没有当前请求的模拟");
                    return response;
                }

                // 获取 policy 文件名
                policyFileName = maddpgSimulationMapper.getPolicyFileNameBySimulationId(userId, unlockSimulationId);
                queryFileName = simulationFileName;
            }

            // 加载 JSON 配置文件
            String filePath = ROOT_FILE_PATH + userId + "\\SimulationResult\\unlock_result\\" +  city + "\\" + queryFileName + "\\data.json";
            File jsonFile = new File(filePath);

            if (!jsonFile.exists()) {
                response.put("status", false);
                response.put("msg", "缺少必要的模拟数据文件：" + filePath);
                return response;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> paraJson = objectMapper.readValue(new FileReader(jsonFile), Map.class);

            // 提取参数
            double R0 = Double.parseDouble(paraJson.get("R0").toString());
            double I_H_para = Double.parseDouble(paraJson.get("I_H_para").toString());
            double I_R_para = Double.parseDouble(paraJson.get("I_R_para").toString());
            double H_R_para = Double.parseDouble(paraJson.get("H_R_para").toString());
            String I_input = paraJson.get("I_input").toString();
            String regionList = paraJson.get("region_list").toString();
            int simulationDays = Integer.parseInt(paraJson.get("simulation_days").toString());
            String simulationCity = paraJson.get("simulation_city").toString();

            // 开始模拟
            String msg = "start MADDPG_simulate";

            // 检查模拟所需的文件是否存在
            if (!new File(ROOT_FILE_PATH + userId + "\\" + city + "\\city.shp").exists()) {
                msg = "缺少网格文件";
            } else if (!new File(ROOT_FILE_PATH + userId + "\\" + city + "\\population.npy").exists()) {
                msg = "缺少人口文件";
            }

            if ("start MADDPG_simulate".equals(msg)) {
                logger.info("MADDPG_start!");
                // 以异步方式开始模拟
                executorService.submit(() -> {
                    try {
                        // 执行实际的模拟任务
                        simulationMADDPGLockTask(paraJson, policyFileName, queryFileName, R0, I_H_para, I_R_para, H_R_para, I_input, regionList, simulationDays, simulationCity);
                    } catch (Exception e) {
                        logger.severe("模拟任务失败: " + e.getMessage());
                    }
                });
            }

            response.put("status", true);
            response.put("msg", msg);

        } catch (IOException e) {
            response.put("status", false);
            response.put("msg", "读取数据文件失败: " + e.getMessage());
        } catch (Exception e) {
            response.put("status", false);
            response.put("msg", "模拟过程中发生错误: " + e.getMessage());
        }

        return response;
    }*/

}


