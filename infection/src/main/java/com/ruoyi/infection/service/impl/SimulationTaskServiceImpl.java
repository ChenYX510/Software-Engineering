package com.ruoyi.infection.service.impl;

import com.ruoyi.infection.domain.SimulationTask;
import com.ruoyi.infection.service.ISimulationTaskService;
import com.ruoyi.infection.mapper.SimulationTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class SimulationTaskServiceImpl implements ISimulationTaskService {
    @Autowired
    private SimulationTaskMapper simulationTaskMapper;

    private static final String ROOT_FILE_PATH = System.getProperty("user.dir") + "\\testUser\\";

    private static final Logger logger = Logger.getLogger(SimulationTaskServiceImpl.class.getName());
    private final ExecutorService executor = Executors.newFixedThreadPool(2); // 线程池
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> unlockSimulationTask(SimulationTask request) {
        long userId = request.getUserId();

        double R0 = request.getR0();
        double I_H_para = request.getI_H_para();
        double I_R_para = request.getI_R_para();
        double H_R_para = request.getH_R_para();
        String I_input = request.getI_input();
        String region_list = request.getRegionList();
        int simulation_days = request.getSimulationDays();
        String simulation_city = request.getSimulationCity();

        // 移除转义字符，得到有效的 JSON 格式
        I_input = I_input.replace("\\", "\\\\");

        System.out.println("I_input: " + I_input);

        // 检查所需文件是否存在
        String msg = "start simulate";
        System.out.println(ROOT_FILE_PATH + userId + "\\" + simulation_city + "\\city.shp");
        if (!new File(ROOT_FILE_PATH + userId + "\\" + simulation_city + "\\city.shp").exists()) {
            msg = "缺少网格文件";
        } else if (!new File(ROOT_FILE_PATH + userId + "\\" + simulation_city + "\\population.npy").exists()) {
            msg = "缺少人口文件";
        } /*else if (!new File(ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\OD.npy").exists()) {
            msg = "缺少OD数据文件";
        }*/

        Map<String, Object> result = new HashMap<>();
        String curDirName = "";
        int resultId = 0;
        if ("start simulate".equals(msg)) {
            curDirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
            resultId = getResultId(userId, "none_type");
            // 在数据库创建一条新的模拟记录
            boolean dbSaveStatus = saveRecordDatabase(userId, resultId, "none_type", curDirName, simulation_city, 1);
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
                result.put("status", true);
                msg = "Simulation started successfully";
            } else {
                result.put("status", false);
                msg = "Failed to start simulation";
                result.put("msg", msg);
                return result;
            }
        } else {
            result.put("status", false);
            result.put("msg", msg);
            return result;
        }
        // 更新数据库
        boolean dbUpdateStatus = modifyStatus(resultId, "none_type", curDirName);
        if (dbUpdateStatus) {
            logger.info("Database status updated successfully for simulation: " + simulation_city);
        } else {
            logger.warning("Failed to update database status for simulation: " + simulation_city);
        }
        result.put("msg", msg);

        // 开始进行强化学习的策略生成
        String policyCurDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
        // 调用python脚本
        boolean MADDPGStatus = generateMADDPGPolicy(ROOT_FILE_PATH + "maddpgPolicy.py", userId, simulation_city, simulation_days, policyCurDir, curDirName);
        if (MADDPGStatus) {
            logger.info("MADDPG policy started successfully");
        } else {
            logger.info("MADDPG policy failed");
        }

        // 记录决策结束的时间
        String MADDPG_policy_end_time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
        dbUpdateStatus = updatePolicyRecords(userId, policyCurDir, policyCurDir, MADDPG_policy_end_time, resultId);
        if (dbUpdateStatus) {
            logger.info("Database status updated successfully for simulation: " + simulation_city);
        } else {
            logger.warning("Failed to update database status for simulation: " + simulation_city);
        }

        return result;
    }

    // 手动封控下的传染病模拟
    public Map<String, Object> lockSimulationTask(SimulationTask request) {
        long userId = request.getUserId();

        double R0 = request.getR0();
        double I_H_para = request.getI_H_para();
        double I_R_para = request.getI_R_para();
        double H_R_para = request.getH_R_para();
        String I_input = request.getI_input();
        String region_list = request.getRegionList();
        String lock_area = request.getLock_area();
        int lock_day = request.getLock_day();
        int simulation_days = request.getSimulationDays();
        String simulation_city = request.getSimulationCity();

        // 移除转义字符，得到有效的 JSON 格式
        I_input = I_input.replace("\\", "\\\\");
        lock_area = lock_area.replace("\\", "\\\\");

        // 检查所需文件是否存在
        String msg = "start simulate";
        System.out.println(ROOT_FILE_PATH + userId + "\\" + simulation_city + "\\city.shp");
        if (!new File(ROOT_FILE_PATH + userId + "\\" + simulation_city + "\\city.shp").exists()) {
            msg = "缺少网格文件";
        } else if (!new File(ROOT_FILE_PATH + userId + "\\" + simulation_city + "\\population.npy").exists()) {
            msg = "缺少人口文件";
        } /*else if (!new File(ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\OD.npy").exists()) {
            msg = "缺少OD数据文件";
        }*/

        Map<String, Object> result = new HashMap<>();
        String curDirName = "";
        int resultId = 0;
        if ("start simulate".equals(msg)) {
            // 根据当前时间设置文件夹名称
            curDirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
            resultId = getResultId(userId, "lock_type");
            // 在数据库创建一条新的模拟记录
            boolean dbSaveStatus = saveRecordDatabase(userId, resultId, "lock_type", curDirName, simulation_city, 1);
            if (dbSaveStatus) {
                logger.info("Database record saved successfully for simulation: " + simulation_city);
            } else {
                logger.warning("Failed to save database record for simulation: " + simulation_city);
            }
            // 启动模拟任务
            // 调用 Python 脚本
            boolean pythonExecutionStatus = lockSimulationPythonScript(
                    ROOT_FILE_PATH + "lockSimulate.py",
                    R0, I_H_para, I_R_para, H_R_para, I_input, region_list, simulation_days, simulation_city, curDirName,
                    userId, lock_area, lock_day
            );

            if (pythonExecutionStatus) {
                result.put("status", true);
                msg = "Simulation started successfully";
            } else {
                result.put("status", false);
                msg = "Failed to start simulation";
                result.put("msg", msg);
                return result;
            }
        } else {
            result.put("status", false);
            result.put("msg", msg);
            return result;
        }
        // 更新数据库
        boolean dbUpdateStatus = modifyStatus(resultId, "lock_type", curDirName);
        if (dbUpdateStatus) {
            logger.info("Database status updated successfully for simulation: " + simulation_city);
        } else {
            logger.warning("Failed to update database status for simulation: " + simulation_city);
        }
        result.put("msg", msg);

        return result;

    }

    // 强化学习动态封控下的模拟
    public Map<String, Object> MADDPGSimulationTask(SimulationTask request){
        Map<String, Object> result = new HashMap<>();

        String city = request.getSimulationCity();
        String simulationFileName = request.getSimulationFileName();
        long userId = request.getUserId();

        // 如果没有文件名称，则默认取最新一次的模拟
        if (simulationFileName == null || simulationFileName.isEmpty()) {
            simulationFileName = "latestRecord"; // 默认为最新记录
        }

        // 无封控模拟id
        Integer unlockSimulationId = null;
        // 强化学习策略id
        Integer policyId = null;
        // 策略文件名称
        String policyFileName = null;
        // 用于查询的文件名称，就是当前无封控模拟结果的文件名
        String queryFileName = null;

        try {
            if ("latestRecord".equals(simulationFileName)) {
                // 获取最新的模拟文件 ID
                unlockSimulationId = simulationTaskMapper.getLatestSimulationIdFromPolicy(userId);

                if (unlockSimulationId == null||unlockSimulationId==0) {
                    result.put("status", false);
                    result.put("msg", "请先进行传染病模拟");
                    return result;
                }

                // 根据用户id和unlockSimulationId获取最新的policyId
                policyId = simulationTaskMapper.getPolicyIdByUnlockSimulationId(userId, unlockSimulationId);
                // 根据id获取 policy 文件名
                policyFileName = simulationTaskMapper.getPolicyFileNameByPolicyId(userId, policyId);
                // 获取无封控模拟结果的文件名
                queryFileName = simulationTaskMapper.getQueryFileNameBySimulationId(userId, unlockSimulationId);
            } else {
                // 根据传入的文件名获取 simulation_id
                unlockSimulationId = simulationTaskMapper.getSimulationIdByFilePath(userId, simulationFileName);

                if (unlockSimulationId == null || unlockSimulationId == 0) {
                    result.put("status", false);
                    result.put("msg", "没有当前请求的模拟");
                    return result;
                }

                // 根据用户id和unlockSimulationId获取最新的policyId
                policyId = simulationTaskMapper.getPolicyIdByUnlockSimulationId(userId, unlockSimulationId);
                // 根据id获取 policy 文件名
                policyFileName = simulationTaskMapper.getPolicyFileNameByPolicyId(userId, policyId);
                queryFileName = simulationFileName;
            }

            // 加载 JSON 配置文件
            String filePath = ROOT_FILE_PATH + userId + "\\SimulationResult\\unlock_result\\" +  city + "\\" + queryFileName + "\\data.json";
            File jsonFile = new File(filePath);

            if (!jsonFile.exists()) {
                result.put("status", false);
                result.put("msg", "缺少必要的模拟数据文件：" + filePath);
                return result;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> paraJson = objectMapper.readValue(new FileReader(jsonFile), Map.class);

            // 提取参数
            double R0 = Double.parseDouble(paraJson.get("R0").toString());
            double I_H_para = Double.parseDouble(paraJson.get("I_H_para").toString());
            double I_R_para = Double.parseDouble(paraJson.get("I_R_para").toString());
            double H_R_para = Double.parseDouble(paraJson.get("H_R_para").toString());

            // 获取 I_input 并转换为 JSON 字符串
            Map<String, Integer> I_inputMap = (Map<String, Integer>) paraJson.get("I_input");
            String I_input = objectMapper.writeValueAsString(I_inputMap);  // 转换为 JSON 格式字符串

            // 获取 regionList 并转换为 JSON 字符串
            Map<String, Integer> regionListMap = (Map<String, Integer>) paraJson.get("region_list");
            String regionList = objectMapper.writeValueAsString(regionListMap);  // 转换为 JSON 格式字符串
            
            int simulationDays = Integer.parseInt(paraJson.get("simulation_days").toString());
            String simulationCity = paraJson.get("simulation_city").toString();

            System.out.println("I_input: " + I_input);  // 输出 I_input 为 JSON 格式字符串


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
                // 模拟结果文件的储存目录，就是开始模拟的时间
                curDirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
                resultId = getResultId(userId, "MADDPG_type");
                // 在数据库创建一条新的模拟记录
                boolean dbSaveStatus = saveRecordDatabase(userId, resultId, "MADDPG_type", curDirName, simulationCity, policyId);
                if (dbSaveStatus) {
                    logger.info("Database record saved successfully for simulation: " + simulationCity);
                } else {
                    logger.warning("Failed to save database record for simulation: " + simulationCity);
                }
                // 启动模拟任务
                //startSimulationTask(request);
                //executor.submit(() -> runSimulationTask(request));
                // result.put("status", true);
                // 调用 Python 脚本
                boolean pythonExecutionStatus = MADDPGSimulationPythonScript(
                        ROOT_FILE_PATH + "MADDPGSimulate.py", // 替换为 Python 脚本路径
                        R0, I_H_para, I_R_para, H_R_para, I_input, regionList, simulationDays, simulationCity, curDirName, userId, policyFileName, queryFileName
                );

                if (pythonExecutionStatus) {
                    result.put("status", true);
                    msg = "Simulation started successfully";
                } else {
                    result.put("status", false);
                    msg = "Failed to start simulation";
                    result.put("msg", msg);
                    return result;
                }
            }else {
                result.put("status", false);
                result.put("msg", msg);
                return result;
            }
            // 更新数据库
            boolean dbUpdateStatus = modifyStatus(resultId, "MADDPG_type", curDirName);
            if (dbUpdateStatus) {
                logger.info("Database status updated successfully for simulation: " + simulationCity);
            } else {
                logger.warning("Failed to update database status for simulation: " + simulationCity);
            }
            result.put("msg", msg);

        } catch (Exception e) {
            result.put("status", false);
            result.put("msg", "模拟过程中发生错误: " + e.getMessage());
        }

        return result;
    }

    // 调用python脚本的具体实现
    private boolean unlockSimulationPythonScript(
            String scriptPath,
            double R0,
            double I_H_para,
            double I_R_para,
            double H_R_para,
            String I_input,
            String regionList,
            int simulationDays,
            String simulationCity,
            String curDirName,
            long userId) {

        // 指定虚拟环境的 Python 解释器路径
        String pythonExecutable = "C:\\Users\\Lenovo\\anaconda3\\envs\\myenv39\\python.exe"; // 替换为你的虚拟环境路径

        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable, scriptPath, // 使用虚拟环境的 Python 解释器
                    "--R0", String.valueOf(R0),
                    "--I_H_para", String.valueOf(I_H_para),
                    "--I_R_para", String.valueOf(I_R_para),
                    "--H_R_para", String.valueOf(H_R_para),
                    // 注意这个传参形式，必须要双斜杠才能保证json中的双引号传到python
                    "--I_input", "\"" + I_input.replace("\"", "\\\"") + "\"", // 通过replace确保转义双引号
                    "--region_list", "\"" + regionList.replace("\"", "\\\"") + "\"",
                    "--simulation_days", String.valueOf(simulationDays),
                    "--simulation_city", simulationCity,
                    "--cur_dir_name", curDirName
            );


            // 设置环境变量和工作目录
            processBuilder.directory(new File(ROOT_FILE_PATH + userId));
            processBuilder.redirectErrorStream(true);


            // 启动进程
            Process process = processBuilder.start();

            // 捕获输出
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    logger.info(scanner.nextLine());
                }
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            return exitCode == 0; // 返回是否成功
        } catch (Exception e) {
            logger.severe("Error executing Python script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean lockSimulationPythonScript(
            String scriptPath,
            double R0,
            double I_H_para,
            double I_R_para,
            double H_R_para,
            String I_input,
            String regionList,
            int simulationDays,
            String simulationCity,
            String curDirName,
            long userId,
            String lock_area,
            int lock_day) {

        // 指定虚拟环境的 Python 解释器路径
        String pythonExecutable = "C:\\Users\\Lenovo\\anaconda3\\envs\\myenv39\\python.exe"; // 替换为你的虚拟环境路径

        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable, scriptPath, // 使用虚拟环境的 Python 解释器
                    "--R0", String.valueOf(R0),
                    "--I_H_para", String.valueOf(I_H_para),
                    "--I_R_para", String.valueOf(I_R_para),
                    "--H_R_para", String.valueOf(H_R_para),
                    // 注意这个传参形式，必须要双斜杠才能保证json中的双引号传到python
                    "--I_input", "\"" + I_input.replace("\"", "\\\"") + "\"", // 通过replace确保转义双引号
                    "--region_list", "\"" + regionList.replace("\"", "\\\"") + "\"",
                    "--simulation_days", String.valueOf(simulationDays),
                    "--simulation_city", simulationCity,
                    "--lock_area", "\"" + lock_area.replace("\"", "\\\"") + "\"",
                    "--lock_day", String.valueOf(lock_day),
                    "--cur_dir_name", curDirName
            );


            // 设置环境变量和工作目录
            processBuilder.directory(new File(ROOT_FILE_PATH + userId));
            processBuilder.redirectErrorStream(true);


            // 启动进程
            Process process = processBuilder.start();

            // 捕获输出
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    logger.info(scanner.nextLine());
                }
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            return exitCode == 0; // 返回是否成功
        } catch (Exception e) {
            logger.severe("Error executing Python script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 调用python脚本的具体实现
    private boolean MADDPGSimulationPythonScript(
            String scriptPath,
            double R0,
            double I_H_para,
            double I_R_para,
            double H_R_para,
            String I_input,
            String regionList,
            int simulationDays,
            String simulationCity,
            String curDirName,
            long userId,
            String policyFileName,
            String queryFileName) {

        // 指定虚拟环境的 Python 解释器路径
        String pythonExecutable = "C:\\Users\\Lenovo\\anaconda3\\envs\\myenv39\\python.exe"; // 替换为你的虚拟环境路径

        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable, scriptPath, // 使用虚拟环境的 Python 解释器
                    "--R0", String.valueOf(R0),
                    "--I_H_para", String.valueOf(I_H_para),
                    "--I_R_para", String.valueOf(I_R_para),
                    "--H_R_para", String.valueOf(H_R_para),
                    // 注意这个传参形式，必须要双斜杠才能保证json中的双引号传到python
                    "--I_input", "\"" + I_input.replace("\"", "\\\"") + "\"", // 通过replace确保转义双引号
                    "--region_list", "\"" + regionList.replace("\"", "\\\"") + "\"",
                    "--simulation_days", String.valueOf(simulationDays),
                    "--simulation_city", simulationCity,
                    "--cur_dir_name", curDirName,
                    "--policy_file_name", policyFileName,
                    "--query_file_name", queryFileName
            );

            // 设置环境变量和工作目录
            processBuilder.directory(new File(ROOT_FILE_PATH + userId));
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 捕获输出
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    logger.info(scanner.nextLine());
                }
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            return exitCode == 0; // 返回是否成功
        } catch (Exception e) {
            logger.severe("Error executing Python script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 获取当前模拟id
    public int getResultId(Long userId, String funcType) {
        String resultColumn;
        String resultTable;
        switch (funcType) {
            case "none_type":
                resultColumn = "unlock_result_id";
                resultTable = "infection_unlock_simulation_result";
                break;
            case "lock_type":
                resultColumn = "lock_result_id";
                resultTable = "infection_lock_simulation_result";
                break;
            case "MADDPG_type":
                resultColumn = "maddpg_result_id";
                resultTable = "maddpg_simulation_result";
                break;
            default:
                logger.warning("Invalid funcType provided: " + funcType);
                return -1;
        }
        // 获取当前用户在 user_infection_simulation_result 表中的最大 result_id
        Integer currentMaxResultId = simulationTaskMapper.getMaxResultId(userId, resultTable, resultColumn);
        int newResultId = (currentMaxResultId == null ? 0 : currentMaxResultId) + 1;
        return newResultId;
    }

    // 在数据库中新插入一条模拟记录
    public boolean saveRecordDatabase(Long userId, int newResultId, String funcType, String dirName, String cityName, Integer policyId) {
        String resultTable;
        switch (funcType) {
            case "none_type":
                resultTable = "infection_unlock_simulation_result";
                break;
            case "lock_type":
                resultTable = "infection_lock_simulation_result";
                break;
            case "MADDPG_type":
                resultTable = "maddpg_simulation_result";
                break;
            default:
                logger.warning("Invalid funcType provided: " + funcType);
                return false;
        }

        try {
            // 获取当前用户在 user_infection_simulation_result 表中的最大 result_id
            //Integer currentMaxResultId = simulationTaskMapper.getMaxResultId(userId, resultColumn);
            //int newResultId = (currentMaxResultId == null ? 0 : currentMaxResultId) + 1;
            int rowsInserted = simulationTaskMapper.insertSimulationResult(resultTable, userId, newResultId, dirName, cityName, policyId, "False");
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
        }
    }

    // 在数据库中更新模拟状态
    public boolean modifyStatus(int resultId, String funcType, String dirName) {
        String resultTable;
        String resultColumn;
        // 根据类型选择对应表名
        switch (funcType) {
            case "none_type":
                resultTable = "infection_unlock_simulation_result";
                resultColumn = "unlock_result";
                break;
            case "lock_type":
                resultTable = "infection_lock_simulation_result";
                resultColumn = "lock_result";
                break;
            case "MADDPG_type":
                resultTable = "maddpg_simulation_result";
                resultColumn = "maddpg_result";
                break;
            default:
                logger.warning("Invalid funcType provided: " + funcType);
                return false;
        }

        // 调用 Mapper 层更新状态
        try {
            int rows = simulationTaskMapper.updateTaskStatus(resultTable, resultColumn, "True", dirName, resultId);
            if (rows > 0) {
                logger.info("Successfully updated state for resultId: " + resultId + " in table: " + resultTable);
                return true;
            } else {
                logger.warning("No rows updated for resultId: " + resultId + " in table: " + resultTable);
                return false;
            }
        } catch (Exception e) {
            logger.severe("Error updating task status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 生成 MADDPG 策略
    public boolean generateMADDPGPolicy(String scriptPath, long userId, String simulationCity, int simulationDays, String policyCurDir, String curDirName) {
        // String policyCurDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
        // 指定虚拟环境的 Python 解释器路径
        String pythonExecutable = "C:\\Users\\Lenovo\\anaconda3\\envs\\myenv39\\python.exe"; // 替换为你的虚拟环境路径

        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable, scriptPath, // 使用虚拟环境的 Python 解释器
                    "--simulation_days", String.valueOf(simulationDays),
                    "--simulation_city", simulationCity,
                    "--cur_dir_name", curDirName,
                    "--policy_dir", policyCurDir
            );


            // 设置环境变量和工作目录
            processBuilder.directory(new File(ROOT_FILE_PATH + userId));
            processBuilder.redirectErrorStream(true);


            // 启动进程
            Process process = processBuilder.start();

            // 捕获输出
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    logger.info(scanner.nextLine());
                }
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            return exitCode == 0; // 返回是否成功
        } catch (Exception e) {
            logger.severe("Error executing Python script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // 更新策略记录
    private boolean updatePolicyRecords(long userId, String policyCurDirName, String startTime, String endTime, int simulationCurId) {

        /*// 更新 `maddpg_simulation_result` 表
        Integer maxTimeRecordId = simulationTaskMapper.getMaxMADDPGRecordId(userId);
        int newTimeRecordId = (maxTimeRecordId == null ? 0 : maxTimeRecordId + 1);

        int rowsInsertedTimeRecord = simulationTaskMapper.insertMADDPGSimulationRecord(
                userId, newTimeRecordId, startTime, endTime, "False", simulationCity, null, null
        );
        if (rowsInsertedTimeRecord == 0) {
            logger.warning("Failed to insert into maddpg_simulation_time_record.");
            return false;
        }*/

        // 更新 `MADDPG_policy_record` 表
        Integer maxPolicyRecordId = simulationTaskMapper.getMaxPolicyRecordId(userId);
        // Integer simulationCurId = simulationTaskMapper.getMaxResultId(userId, "infection_unlock_simulation_result", "unlock_result_id");
        int newPolicyRecordId = (maxPolicyRecordId == null ? 0 : maxPolicyRecordId + 1);

        int rowsInsertedPolicyRecord = simulationTaskMapper.insertPolicyRecord(
                newPolicyRecordId, policyCurDirName, userId, simulationCurId, startTime, endTime
        );
        if (rowsInsertedPolicyRecord == 0) {
            logger.warning("Failed to insert into MADDPG_policy_record.");
            return false;
        }

        logger.info("Successfully updated MADDPG records.");
        return true;
    }
}













