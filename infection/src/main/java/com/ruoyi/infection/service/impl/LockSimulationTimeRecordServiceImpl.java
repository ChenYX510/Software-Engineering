package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.LockSimulationTimeRecord;
import com.ruoyi.infection.mapper.LockSimulationTimeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

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
import java.text.SimpleDateFormat;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureCollection;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import java.util.*;
@Service
public class LockSimulationTimeRecordServiceImpl implements ILockSimulationTimeRecordService {
    private static final String ROOT_FILE = System.getProperty("user.dir") + "\\testUser\\";
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
    @Override
    public Map<String, Object> getShpJson(String city,String userId) {
        Map<String, Object> result = new HashMap<>();
        String populationFilePath = ROOT_FILE+userId+"\\"+ city + "\\population.csv";
        String shpFilePath = ROOT_FILE+userId+"\\"+ city +"\\city.shp";

        // 检查人口文件是否存在
        if (!new File(populationFilePath).exists()) {
            result.put("msg", "缺少网格人口文件");
            return result;
        }

        // 检查shp文件是否存在
        if (!new File(shpFilePath).exists()) {
            result.put("msg", "缺少网格文件");
            return result;
        }

        // 加载人口数据
        double[] population = loadPopulation(populationFilePath);

        // 加载shp文件并提取几何数据
        List<Map<String, Object>> geometryArray = new ArrayList<>();
        try {
            // 使用 GeoTools 读取 shp 文件
            File shpFile = new File(shpFilePath);
            FileDataStore dataStore = FileDataStoreFinder.getDataStore(shpFile);
            if (dataStore == null) {
                result.put("msg", "无法读取 Shapefile");
                return result;
            }

            // 获取 FeatureSource（矢量数据源）
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(dataStore.getTypeNames()[0]);
            FeatureCollection<SimpleFeatureType, SimpleFeature> features = featureSource.getFeatures();

            // 遍历 FeatureCollection
            try (FeatureIterator<SimpleFeature> iterator = features.features()) {
                int index = 0;
                while (iterator.hasNext() && index < population.length) {
                    SimpleFeature feature = iterator.next();
                    Geometry geom = (Geometry) feature.getDefaultGeometry();

                    Map<String, Object> jsonItem = new HashMap<>();
                    jsonItem.put("geom", geom.toString());
                    jsonItem.put("grid_population", population[index]);
                    jsonItem.put("gid", index);

                    geometryArray.add(jsonItem);
                    index++;
                }
            }

            result.put("data", geometryArray);
            result.put("msg", "grid_shp_data");

        } catch (IOException e) {
            result.put("msg", "读取 shp 文件失败：" + e.getMessage());
        }
        return result;
    }

    // 加载人口数据
    private double[] loadPopulation(String populationFilePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(populationFilePath));
            double[] population = new double[lines.size()];
            for (int i = 0; i < lines.size(); i++) {
                population[i] = Double.parseDouble(lines.get(i).trim());
            }
            return population;
        } catch (IOException e) {
            throw new RuntimeException("加载人口文件失败: " + e.getMessage());
        }
    }
    @Override
    public Map<String, Object> getPolicyResult(String city, int policyDay, int policyTime, String simulationFileName,String userId) {
        int policyHour = (policyDay - 1) * 24 + (policyTime - 1);
        int policyHourPre = policyHour - 3;
        Long simulationId;
        String policyFileName;
        if ("latestRecord".equals(simulationFileName)) {
            simulationId = lockSimulationTimeRecordMapper.getLatestSimulationId(userId);
            if (simulationId == null) {
                return Map.of("msg", "没有最新的模拟记录");
            }
            policyFileName = lockSimulationTimeRecordMapper.getPolicyFilePathBySimulationId(simulationId);
        } else {
            simulationId = lockSimulationTimeRecordMapper.getSimulationIdByFilePath(simulationFileName,userId);
            if (simulationId == null) {
                return Map.of("msg", "没有模拟记录");
            }
            policyFileName = lockSimulationTimeRecordMapper.getPolicyFilePathBySimulationId(simulationId);
        }
        String filePath = ROOT_FILE+userId+"\\grid_coefficient\\" + city + "\\" + policyFileName + "\\" + policyHourPre + "-" + policyHour + "_grid.shp";

        if (Files.exists(Paths.get(filePath))) {
            try {
                return processShapefile(filePath);
            } catch (IOException e) {
                return Map.of("msg", "读取文件时发生错误：" + e.getMessage());
            }
        } else {
            return Map.of("msg", "没有当前结果，请先进行传染病模拟");
        }
    }
     /**
     * 使用 GeoTools 读取 SHP 文件并提取 "quota" 字段
     */
    private Map<String, Object> processShapefile(String filePath) throws IOException {
        FileDataStore store = FileDataStoreFinder.getDataStore(new File(filePath));
        SimpleFeatureSource featureSource = store.getFeatureSource();

        FeatureCollection<?, ?> collection = featureSource.getFeatures();
        List<Object> quotaList = new ArrayList<>();

        try (FeatureIterator<?> features = collection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = (SimpleFeature) features.next();
                // 假设 SHP 文件中有名为 "quota" 的字段
                Object quotaValue = feature.getAttribute("quota");
                if (quotaValue != null) {
                    quotaList.add(quotaValue);
                }
            }
        } finally {
            store.dispose();
        }

        return Map.of("quota", quotaList);
    }
  @Override
    public Map<String, Object> getMADDPGSimulationResult(String city, int date, int time, String simulationFileName,String userId) {
        Map<String, Object> response = new HashMap<>();
        String dir =ROOT_FILE+userId+"\\" +"SimulationResult" +"\\MADDPG_result\\"+ city + "\\";

        // Determine the directory or file name
        if ("latestRecord".equals(simulationFileName)) {
            Long curId = lockSimulationTimeRecordMapper.getLatestRecordId(city,userId);
            if (curId == null || curId == -1) {
                response.put("msg", "没有最新的模拟记录");
                return response;
            }
            String queryFileName = lockSimulationTimeRecordMapper.getFilepathById(curId);
            dir += queryFileName + "\\";
        } else {
            dir += simulationFileName + "\\";
        }

        int curHour = (date - 1) * 24 + (time - 1);
        File simulationFile = new File(dir + "simulation_DSIHR_result_" + curHour + ".csv");
        File directory = new File(dir);

        int num_result = 0;
        if (directory.exists() && directory.isDirectory()) {
            // Count valid result files in the directory
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().startsWith("simulation_DSIHR_result_") && file.getName().endsWith(".csv")) {
                        num_result++;
                    }
                }
            }
        }
        if (num_result<=0) {
            response.put("state", "not completed");
            response.put("num_result", num_result);
            return response;
        }
        else{
        if (!simulationFile.exists()) {
            response.put("state", "not completed");
            response.put("num_result", num_result);
        } else {
            List<Double> S_data = new ArrayList<>();
            List<Double> I_data = new ArrayList<>();
            List<Double> H_data = new ArrayList<>();
            List<Double> R_data = new ArrayList<>();
            List<Double> newInfected_data = new ArrayList<>();
            List<Double> total_num_data = new ArrayList<>();
            // Parse CSV file
            try (Reader reader = new FileReader(simulationFile)) {
                Iterable<CSVRecord> records = CSVFormat.DEFAULT
                .withHeader("Column1","geometry", "S", "I", "H", "R", "new_infected", "total_num") // 为所有列指定名称
                .withSkipHeaderRecord(true) // 表头仍然作为数据解析的依据
                .withTrim()
                .parse(reader);
                for (CSVRecord record : records) {
                    // 第二列是 POLYGON，跳过或处理
                    S_data.add(Double.parseDouble(record.get("S")));
                    I_data.add(Double.parseDouble(record.get("I")));
                    H_data.add(Double.parseDouble(record.get("H")));
                    R_data.add(Double.parseDouble(record.get("R")));
                    newInfected_data.add(Double.parseDouble(record.get("new_infected")));
                    total_num_data.add(Double.parseDouble(record.get("total_num")));
                }

                response.put("state", "current task completed");
                response.put("S_data", S_data);
                response.put("I_data", I_data);
                response.put("H_data", H_data);
                response.put("R_data", R_data);
                response.put("newInfected_data", newInfected_data);
                response.put("total_num_data", total_num_data);
                response.put("num_result", num_result);

            } catch (IOException e) {
                response.put("state", "file read error");
                response.put("error", e.getMessage());
            }
        }
    }

        return response;
    }
}