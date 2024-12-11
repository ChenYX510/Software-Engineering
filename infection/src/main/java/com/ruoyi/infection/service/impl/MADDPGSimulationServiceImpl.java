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
import java.io.FileReader;


@Service
public class MADDPGSimulationServiceImpl  implements IMADDPGSimulationService {
    @Autowired
    private MADDPGSimulationMapper maddpgSimulationMapper;
    private static final String ROOT_FILE_PATH = System.getProperty("user.dir") + "\\testUser\\";

    private static final Logger logger = Logger.getLogger(SimulationTaskServiceImpl.class.getName());

    @Override
    public Map<String, Object> MADDPGSimulation(MADDPGSimulation request){
        /*Map<String, Object> response = new HashMap<>();

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
                unlockSimulationId = maddpgSimulationMapper.getLatestSimulationId(userId);

                if (unlockSimulationId == null||unlockSimulationId==0) {
                    response.put("status", false);
                    response.put("msg", "请先进行传染病模拟");
                    return response;
                }

                // 根据id获取 policy 文件名
                policyFileName = maddpgSimulationMapper.getPolicyFileNameBySimulationId(userId, unlockSimulationId);
                // 获取无封控模拟结果的文件名
                queryFileName = maddpgSimulationMapper.getQueryFileNameBySimulationId(userId, unlockSimulationId);
            } else {
                // 根据传入的文件名获取 simulation_id
                unlockSimulationId = maddpgSimulationMapper.getSimulationIdByFilePath(userId, simulationFileName);

                if (unlockSimulationId == null || unlockSimulationId == 0) {
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

            String curDirName = "";
            int resultId = 0;
            if ("start MADDPG_simulate".equals(msg)) {
                logger.info("MADDPG_start!");
                curDirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
                resultId = getResultId(userId);
                // 在数据库创建一条新的模拟记录
                boolean dbSaveStatus = saveRecordDatabase(userId, resultId, "none_type", curDirName, simulation_city);
                if (dbSaveStatus) {
                    logger.info("Database record saved successfully for simulation: " + simulation_city);
                } else {
                    logger.warning("Failed to save database record for simulation: " + simulation_city);
                }
                // 启动模拟任务
                //startSimulationTask(request);
                //executor.submit(() -> runSimulationTask(request));
                // result.put("status", true);
                // 调用 Python 脚本
                boolean pythonExecutionStatus = unlockSimulationPythonScript(
                        ROOT_FILE_PATH + "simulate.py", // 替换为 Python 脚本路径
                        R0, I_H_para, I_R_para, H_R_para, I_input, region_list, simulation_days, simulation_city, curDirName, userId
                );

                if (pythonExecutionStatus) {
                    response.put("status", true);
                    msg = "Simulation started successfully";
                } else {
                    response.put("status", false);
                    msg = "Failed to start simulation";
                    response.put("msg", msg);
                    return response;
                }
            }else {
                response.put("status", false);
                response.put("msg", msg);
                return response;
            }

            response.put("status", true);
            response.put("msg", msg);

        } catch (Exception e) {
            response.put("status", false);
            response.put("msg", "模拟过程中发生错误: " + e.getMessage());
        }

        return response;
    }

    // 获取当前模拟id
    public int getResultId(Long userId) {
        String resultColumn;
        String resultTable;
        resultColumn = "maddpg_result_id";
        resultTable = "maddpg_simulation_result";
        // 获取当前用户在 user_infection_simulation_result 表中的最大 result_id
        Integer currentMaxResultId = MADDPGSimulationMapper.getMaxResultId(userId, resultTable, resultColumn);
        int newResultId = (currentMaxResultId == null ? 0 : currentMaxResultId) + 1;
        return newResultId;
    }

    // 在数据库中新插入一条模拟记录
    public boolean saveRecordDatabase(Long userId, int newResultId, String funcType, String dirName, String cityName) {
        String resultTable;
        resultTable = "maddpg_simulation_result";

        try {
            // 获取当前用户在 user_infection_simulation_result 表中的最大 result_id
            //Integer currentMaxResultId = simulationTaskMapper.getMaxResultId(userId, resultColumn);
            //int newResultId = (currentMaxResultId == null ? 0 : currentMaxResultId) + 1;

            // 在对应的结果表中插入新记录
           // int rowsInserted = MADDPGSimulationMapper.insertSimulationResult(resultTable, userId, newResultId, dirName, cityName, "False");
            if (rowsInserted <= 0) {
                logger.warning("Failed to insert record into " + resultTable);
                return false;
            } else {
                logger.info("Successfully updated database");
                return true;
            }
        } catch (Exception e) {
            logger.severe("Error saving record database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }*/
    }

}


