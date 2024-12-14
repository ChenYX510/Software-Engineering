package com.ruoyi.infection.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.SimulationcityRecord;
import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.mapper.SimulationRecordMapper;
import com.ruoyi.infection.mapper.LockSimulationTimeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.ruoyi.infection.service.ISimulationRecordService;
import org.locationtech.jts.geom.Geometry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class SimulationRecordServiceImpl implements ISimulationRecordService {
    private static final String ROOT_FILE = System.getProperty("user.dir") + "\\testUser\\";
    private static final String[] CITY_NAMES = {
            "shanghai", "chongqing", "guangzhou", "wulumuqi",
            "ningbo", "dongying", "weihai", "zibo",
            "lianyungang", "wuxi", "ezhou", "sihui"
    };
    @Autowired
    private SimulationRecordMapper simulationRecordMapper;
    private LockSimulationTimeRecordMapper LOCKsimulationRecordMapper;
    @Override
    public List<Long> getIdsByCity(String city,String userId) {
        return simulationRecordMapper.selectIdsByCity(city);
    }
       @Override
    public List<CitySimulationResult> getCitySimulationResults(String userId) {
        String dirPath = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"unlock_result"+"\\";
        List<CitySimulationResult> citySimulationResults = new ArrayList<>();

        for (String city : CITY_NAMES) {
            String dirr = dirPath + city;
            File dir = new File(dirr);
            int numResults = dir.exists() ? dir.list().length : 0;

            List<SimulationcityRecord> simulationRecords = simulationRecordMapper.selectSimulationRecordsByCity(city,userId);

            for (SimulationcityRecord record : simulationRecords) {
                String dataFilePath = dirr + "\\" + record.getSimulationTime() + "\\data.json";//这个地方还有待商榷
                File dataFile = new File(dataFilePath);

                if (dataFile.exists()) {
                    try {
                        // 读取 JSON 文件的内容并转换为字符串
                        String jsonContent = new String(Files.readAllBytes(Paths.get(dataFilePath)));
                        record.setParaJson(jsonContent);  // 将 JSON 内容存储在 paraJson 字段中
                    } catch (IOException e) {
                        record.setParaJson("读时出错 JSON file: " + e.getMessage());
                    }
                } else {
                    record.setParaJson("没有找到 data.json 文件");
                }
            }
           
            CitySimulationResult result = new CitySimulationResult();
            result.setCity(city);
            result.setSimulationRecordNum(numResults);
            result.setSimulationRecord(simulationRecords);

            citySimulationResults.add(result);
        }

        return citySimulationResults;
    }
    @Override
    public List<CitySimulationResult> getCitySimulationLockResults(String userId) {
        String dirPath = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"lock_result"+"\\";
        List<CitySimulationResult> citySimulationResults = new ArrayList<>();

        for (String city : CITY_NAMES) {
            String dirr = dirPath + city;
            File dir = new File(dirr);
            int numResults = dir.exists() ? dir.list().length : 0;

            List<SimulationcityRecord> simulationRecords = simulationRecordMapper.selectSimulationLockRecordsByCity(city,userId);

            for (SimulationcityRecord record : simulationRecords) {
                String dataFilePath = dirr + "\\" + record.getSimulationTime() + "\\data.json";//这个地方还有待商榷
                File dataFile = new File(dataFilePath);

                if (dataFile.exists()) {
                    try {
                        // 读取 JSON 文件的内容并转换为字符串
                        String jsonContent = new String(Files.readAllBytes(Paths.get(dataFilePath)));
                        record.setParaJson(jsonContent);  // 将 JSON 内容存储在 paraJson 字段中
                    } catch (IOException e) {
                        record.setParaJson("读时出错 JSON file: " + e.getMessage());
                    }
                } else {
                    record.setParaJson("没有找到 data.json 文件");
                }
            }

            CitySimulationResult result = new CitySimulationResult();
            result.setCity(city);
            result.setSimulationRecordNum(numResults);
            result.setSimulationRecord(simulationRecords);

            citySimulationResults.add(result);
        }

        return citySimulationResults;
    }
    @Override
    public List<CitySimulationResult> getCitySimulationMADDPGResults(String userId) {
        String dirPath = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"MADDPG_result"+"\\";
        List<CitySimulationResult> citySimulationResults = new ArrayList<>();

        for (String city : CITY_NAMES) {
            String dirr = dirPath + city;
            File dir = new File(dirr);
            int numResults = dir.exists() ? dir.list().length : 0;

            List<SimulationcityRecord> simulationRecords = simulationRecordMapper.selectSimulationMADDPGRecordsByCity(city,userId);

            for (SimulationcityRecord record : simulationRecords) {
                String dataFilePath = dirr + "\\" + record.getSimulationTime() + "\\data.json";//这个地方还有待商榷
                File dataFile = new File(dataFilePath);

                if (dataFile.exists()) {
                    try {
                        // 读取 JSON 文件的内容并转换为字符串
                        String jsonContent = new String(Files.readAllBytes(Paths.get(dataFilePath)));
                        record.setParaJson(jsonContent);  // 将 JSON 内容存储在 paraJson 字段中
                    } catch (IOException e) {
                        record.setParaJson("读时出错 JSON file: " + e.getMessage());
                    }
                } else {
                    record.setParaJson("没有找到 data.json 文件");
                }
            }

            CitySimulationResult result = new CitySimulationResult();
            result.setCity(city);
            result.setSimulationRecordNum(numResults);
            result.setSimulationRecord(simulationRecords);

            citySimulationResults.add(result);
        }

        return citySimulationResults;
    }
        @Override
    public Map<String, Object> getSimulationResult(String city, int simulationDay, int simulationHour, String simulationFileName,String userId) {
        Map<String, Object> result = new HashMap<>();
        String dirPath = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"unlock_result"+"\\";
        dirPath = dirPath + city + "\\";
        int targetSimulationHour = (simulationDay - 1) * 24 + (simulationHour - 1);

        // 获取最新的模拟记录文件夹
        if ("latestRecord".equals(simulationFileName)) {
            Long latestId = simulationRecordMapper.findLatestIdByCity(city,userId);
            if (latestId == null) {
                result.put("msg", "没有最新的模拟记录");
                return result;
            }
            simulationFileName = simulationRecordMapper.findFilePathById(latestId);
        }

        dirPath += simulationFileName + "\\";

        File directory = new File(dirPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                int numResult = (files.length - 1) / 2; // 计算已生成的文件数量

                // 检查目标 CSV 文件是否存在
                String fileName = "SIHR_" + targetSimulationHour + ".csv";
                File resultFile = new File(dirPath + fileName);
                if (resultFile.exists()) {
                    // 读取 CSV 文件内容
                    readCSVFile(resultFile, result);
                    result.put("state", "current task completed");
                } else {
                    result.put("state", "not completed");
                }

                // 读取模拟的 JSON 文件
                readSimulationJson(dirPath + "data.json", result, numResult);
            }
        } else {
            result.put("state", "no simulationResult");
        }

        return result;
    }
    @Override
    public Map<String, Object> getLockSimulationResult(String city, int simulationDay, int simulationHour, String simulationFileName,String userId) {
        Map<String, Object> result = new HashMap<>();
        String dirPath = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"lock_result"+"\\";
        dirPath = dirPath + city + "\\";
        int targetSimulationHour = (simulationDay - 1) * 24 + (simulationHour - 1);

        // 获取最新的模拟记录文件夹
        if ("latestRecord".equals(simulationFileName)) {
            Long latestId = simulationRecordMapper.findLockLatestIdByCity(city,userId);
            if (latestId == null) {
                result.put("msg", "没有最新的模拟记录");
                return result;
            }
            simulationFileName = simulationRecordMapper.findLockFilePathById(latestId);
        }

        dirPath += simulationFileName + "\\";

        File directory = new File(dirPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null && files.length > 0) {
                int numResult = (files.length - 1) / 2; // 计算已生成的文件数量

                // 检查目标 CSV 文件是否存在
                String fileName = "SIHR_" + targetSimulationHour + ".csv";
                File resultFile = new File(dirPath + fileName);
                if (resultFile.exists()) {
                    // 读取 CSV 文件内容
                    readCSVFile(resultFile, result);
                    result.put("state", "current task completed");
                } else {
                    result.put("state", "not completed");
                }

                // 读取模拟的 JSON 文件
                readSimulationJson(dirPath + "data.json", result, numResult);
            }
        } else {
            result.put("state", "no simulationResult");
        }

        return result;
    }

    /**
     * 读取 CSV 文件内容并填充到结果中
     */
    private void readCSVFile(File csvFile, Map<String, Object> result) {
        List<Integer> S_data = new ArrayList<>();
        List<Integer> I_data = new ArrayList<>();
        List<Integer> H_data = new ArrayList<>();
        List<Integer> R_data = new ArrayList<>();
        List<Integer> newInfectedData = new ArrayList<>();
        List<Integer> totalNumData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isHeader = true;

            while ((line = br.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // 跳过表头
                    continue;
                }

                String[] values = line.split(",");
                S_data.add(Integer.parseInt(values[0]));
                I_data.add(Integer.parseInt(values[1]));
                H_data.add(Integer.parseInt(values[2]));
                R_data.add(Integer.parseInt(values[3]));
                newInfectedData.add(Integer.parseInt(values[4]));
                totalNumData.add(Integer.parseInt(values[5]));
            }

            result.put("S_data", S_data);
            result.put("I_data", I_data);
            result.put("H_data", H_data);
            result.put("R_data", R_data);
            result.put("newInfected_data", newInfectedData);
            result.put("total_num_data", totalNumData);

        } catch (IOException e) {
            e.printStackTrace();
            result.put("msg", "读取CSV文件失败：" + e.getMessage());
        }
    }

    /**
     * 读取 JSON 文件内容并填充到结果中
     */
    private void readSimulationJson(String jsonFilePath, Map<String, Object> result, int numResult) {
        File jsonFile = new File(jsonFilePath);
        if (jsonFile.exists()) {
            try (FileReader reader = new FileReader(jsonFile)) {
                String content = Files.readString(Paths.get(jsonFilePath));
                JSONObject jsonObject = JSONObject.parseObject(content);
                result.put("para_json", jsonObject);
                int plannedSimulationDays = Integer.parseInt(jsonObject.get("simulation_days").toString());
                int plannedFilesNum = plannedSimulationDays * 24;

                if (plannedFilesNum == numResult) {
                    result.put("state", "all tasks completed");
                }

            } catch (Exception e) {
                e.printStackTrace();
                result.put("msg", "读取JSON文件失败：" + e.getMessage());
            }
        } else {
            result.put("state", "no simulationTask");
        }
    }
    @Override
    public Map<String, Object> getSimulationRiskPoints(String city, int simulationDay, int simulationHour, int thresholdInfected, String simulationFileName,String userId) {
        Map<String, Object> result = new HashMap<>();
        String dir = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"unlock_result"+"\\";
        dir = dir + city + "\\";
        if ("latestRecord".equals(simulationFileName)) {
            Integer maxId = LOCKsimulationRecordMapper.findLatestSimulationId(city,userId);
            if (maxId == null) {
                result.put("msg", "没有最新的模拟记录");
                return result;
            }
            simulationFileName = LOCKsimulationRecordMapper.findFilePathById(maxId,userId);
            dir += simulationFileName + "\\";
        } else {
            dir += simulationFileName + "\\";
        }

        simulationHour = (simulationDay - 1) * 24 + (simulationHour - 1);
        String filePath = dir + "SIHR_" + simulationHour + ".csv";
        List<List<Object>> riskPoints = new ArrayList<>();

        File csvFile = new File(filePath);
        if (csvFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String headerLine = br.readLine(); // 读取并跳过第一行的表头
                String line;

                int index = 0; // 用于跟踪当前行号
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(","); // 根据逗号分隔字段

                    int infected = Integer.parseInt(values[2].trim()); // 假设 I 列是第二列

                    if (infected >= thresholdInfected) {
                        riskPoints.add(Arrays.asList(index, infected));
                    }
                    index++;
                }

                result.put("msg", riskPoints.isEmpty() ? "没有超过阈值的网格" : "success");

            } catch (IOException e) {
                result.put("msg", "读取 CSV 文件失败：" + e.getMessage());
            }
        } else {
            result.put("msg", "没有这个时刻的模拟结果");
        }

        result.put("result", riskPoints);
        return result;
    }
    @Override
    public Map<String, Object> getLockSimulationRiskPoints(String city, int simulationDay, int simulationHour, int thresholdInfected, String simulationFileName,String userId) {
        Map<String, Object> result = new HashMap<>();
        String dir = ROOT_FILE+userId+"\\"+"SimulationResult"+"\\"+"unlock_result"+"\\";
        dir = dir + city + "\\";
        if ("latestRecord".equals(simulationFileName)) {
            Integer maxId = LOCKsimulationRecordMapper.findLockLatestSimulationId(city,userId);
            if (maxId == null) {
                result.put("msg", "没有最新的模拟记录");
                return result;
            }
            simulationFileName = LOCKsimulationRecordMapper.findLockFilePathById(maxId,userId);
            dir += simulationFileName + "\\";
        } else {
            dir += simulationFileName + "\\";
        }

        simulationHour = (simulationDay - 1) * 24 + (simulationHour - 1);
        String filePath = dir + "SIHR_" + simulationHour + ".csv";
        List<List<Object>> riskPoints = new ArrayList<>();

        File csvFile = new File(filePath);
        if (csvFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String headerLine = br.readLine(); // 读取并跳过第一行的表头
                String line;

                int index = 0; // 用于跟踪当前行号
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(","); // 根据逗号分隔字段

                    int infected = Integer.parseInt(values[2].trim()); // 假设 I 列是第二列

                    if (infected >= thresholdInfected) {
                        riskPoints.add(Arrays.asList(index, infected));
                    }
                    index++;
                }

                result.put("msg", riskPoints.isEmpty() ? "没有超过阈值的网格" : "success");

            } catch (IOException e) {
                result.put("msg", "读取 CSV 文件失败：" + e.getMessage());
            }
        } else {
            result.put("msg", "没有这个时刻的模拟结果");
        }

        result.put("result", riskPoints);
        return result;
    }
    @Override
    public Map<String, Object> getgrid_control_policy(String city, String userId) {
        Map<String, Object> result = new HashMap<>();
        String dirPath = ROOT_FILE+userId+"\\"+"grid_coefficient"+"\\";
        dirPath = dirPath + city + "\\";
        File directory = new File(dirPath);
        // 返回的消息和文件数量
        String msg;
        int numFile = 0;
        if (directory.exists() && directory.isDirectory()) {
            // 统计目录中的文件数量
            numFile = directory.listFiles() != null ? directory.listFiles().length : 0;
            msg = (numFile == 0) ? "没有文件" : "success";
        } else {
            // 目录不存在
            msg = "没有文件";
        }
        result.put("msg", msg);
        result.put("num_file", numFile);

        return result;
    }
    @Override
        public Map<String, Object> getCity4LevelName(String city,String userId) {
        Map<String, Object> response = new HashMap<>();
        List<String> nameList = new ArrayList<>();
        String filePath = ROOT_FILE+userId+"\\" + city + "\\upload.shp";
        File file = new File(filePath);

        if (!file.exists()) {
            response.put("msg", "Shapefile 文件不存在");
            response.put("name_list", nameList);
            return response;
        }

        try {
            File files = new File(filePath);
            if (!files.exists()) {
                response.put("msg", "文件不存在");
                return response;
            }

            FileDataStore store = FileDataStoreFinder.getDataStore(files);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            FeatureCollection<?, ?> collection = featureSource.getFeatures();

            try (FeatureIterator<?> features = collection.features()) {
                while (features.hasNext()) {
                    SimpleFeature feature = (SimpleFeature) features.next();
                    String name = (String) feature.getAttribute("index"); // 读取 "index" 列的值
                    nameList.add(name);
                }
            }
            response.put("msg", "success");
            response.put("name_list", nameList);

        } catch (Exception e) {
            response.put("msg", "读取文件时发生错误");
            e.printStackTrace();
        }

        return response;
    }
    @Override
        public Map<String, Object> getDSIHR(String file,double I_H_para,double I_R_para,double H_R_para,String userId) {

            String pythonExecutable = "C:\\Users\\86182\\anaconda3\\python.exe";  // 或者直接指定完整路径
            String scriptPath = "C:\\Users\\86182\\Desktop\\数据库课设\\Software-Engineering\\ruoyi-admin\\testuser\\getDSIHR.py";
            // 处理结果
             // 调用函数
             Map<String, Object> result = callPythonScript(pythonExecutable, scriptPath, I_H_para, I_R_para, H_R_para,file);


        return result;
    }
    public static Map<String, Object> callPythonScript(String pythonExecutable, String scriptPath,
                                                       double I_H_para, double I_R_para, double H_R_para,
                                                       String filepath) {
        ProcessBuilder pb;
        Process process;
        BufferedReader reader;
        StringBuilder output = new StringBuilder();
        Gson gson = new Gson();

        try {
            // 构建命令行参数
            String[] command = {pythonExecutable, scriptPath,
                                Double.toString(I_H_para), Double.toString(I_R_para),
                                Double.toString(H_R_para), filepath};

            // 创建进程构建器并启动进程
            pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);  // 合并标准输出和标准错误流
            process = pb.start();

            // 读取输出
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }

            // 等待进程结束并获取退出码
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Python script exited with error code: " + exitCode);
                return null;  // 或者抛出异常
            }
            System.out.println("Python script output: " + output.toString());
            // 将 JSON 字符串转换为 Map
            return gson.fromJson(output.toString(), HashMap.class);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

