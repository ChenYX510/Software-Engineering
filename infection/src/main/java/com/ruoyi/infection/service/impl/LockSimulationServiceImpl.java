package com.ruoyi.infection.service.impl;

import com.alibaba.fastjson2.support.csv.CSVReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.SimulationRequest;
import com.ruoyi.infection.mapper.LockSimulationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ILockSimulationService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.nio.file.Files;

@Service
public class LockSimulationServiceImpl implements ILockSimulationService {

    @Autowired
    private LockSimulationRecordMapper lockSimulationRecordMapper;
    private static final String ROOT_FILE_PATH = System.getProperty("user.dir") + "\\testUser\\";

    @Override
    public List<Double> getLockEveryHourInfection(String city,String userId, String simulationFileName) {
        String dir = ROOT_FILE_PATH+userId + "\\" + "SimulationResult" +  "\\"+"lock_result\\"+city + "\\";
        if (Objects.equals(simulationFileName, "latestRecord")) {
            List<Long> ids = lockSimulationRecordMapper.selectIdsByCity(city,userId);
            Long maxId = ids.stream().max(Long::compare).orElse(-1L);

            if (maxId == -1) {
                return null; // No records
            }

            String queryFilePath = lockSimulationRecordMapper.selectFilepathById(maxId);
            dir = dir+queryFilePath+"\\";
        } else {
            dir = dir+simulationFileName+"\\";
        }

        // Load data from CSV files
        List<Double> result = new ArrayList<>();// 用于存储每个文件中 "I" 列的总和
        int curHour = 0;
        while (new File(dir + "SIHR_" + curHour + ".csv").exists()) {
            double sum = loadAndSumInfections(dir + "SIHR_" + curHour + ".csv");
            result.add(sum*0.8 );
            curHour++;
        }

        return result;
    }

    @Override
    public List<Double> getEveryHourInfection(String city, String userId,String simulationFileName) {
        String dir = ROOT_FILE_PATH+userId + "\\" + "SimulationResult" +  "\\"+"unlock_result\\"+city + "\\";
        if (Objects.equals(simulationFileName, "latestRecord")) {
            List<Long> ids = lockSimulationRecordMapper.selectIdByCity(city,userId);
            Long maxId = ids.stream().max(Long::compare).orElse(-1L);

            if (maxId == -1) {
                return null; // No records
            }

            String queryFilePath = lockSimulationRecordMapper.selectFilespathById(maxId);
            dir = dir+queryFilePath+"\\";
        } else {
            dir = dir+simulationFileName+"\\";
        }

        // Load data from CSV files
        List<Double> result = new ArrayList<>();
        int curHour = 0;
        while (new File(dir + "SIHR_" + curHour + ".csv").exists()) {
            double sum = loadAndSumInfections(dir + "SIHR_" + curHour + ".csv");
            result.add(sum );
            curHour++;
        }

        return result;
    }
    @Override
    public List<Double> getMADDPGEveryHourInfection(String city, String userId,String simulationFileName) {
        String dir = ROOT_FILE_PATH+userId + "\\" + "SimulationResult" +  "\\"+"MADDPG_result\\"+city + "\\";
        if (Objects.equals(simulationFileName, "latestRecord")) {
            List<Long> ids = lockSimulationRecordMapper.selectMADDPGIdByCity(city,userId);
            Long maxId = ids.stream().max(Long::compare).orElse(-1L);

            if (maxId == -1) {
                return null; // No records
            }

            String queryFilePath = lockSimulationRecordMapper.selectMADDPGFilespathById(maxId);
            dir = dir+queryFilePath+"\\";
        } else {
            dir = dir+simulationFileName+"\\";
        }

        // Load data from CSV files
        List<Double> result = new ArrayList<>();
        int curHour = 0;
        // 循环，直到没有找到文件
        while (true) {
            File file = new File(dir + "simulation_DSIHR_result_" + curHour + ".npy");

            // 检查文件是否存在
            if (!file.exists()) {
                break;
            }
            try {
                // 使用 ND4J 加载 .npy 文件
                INDArray data = Nd4j.createFromNpyFile(file);

                // 获取数据的第三个元素并计算总和（假设数据是一个多维数组）
                double sum = data.getRow(2).sumNumber().doubleValue();

                // 将结果添加到列表
                result.add(sum);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            curHour++;
        }

        return result;
    }



    private double loadAndSumInfections(String filePath) {
        double totalInfections = 0;
        String line;
        String csvSplitBy = ","; // 默认分隔符
        int infectionColumnIndex = -1; // 用于存储感染人数所在的列索引
    
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // 读取第一行（表头）
            String headerLine = br.readLine();
            if (headerLine != null) {
                String[] headers = headerLine.split(csvSplitBy);
    
                // 查找 'I' 列的索引
                for (int i = 0; i < headers.length; i++) {
                    if ("I".equalsIgnoreCase(headers[i].trim())) { // 匹配 'I' 列
                        infectionColumnIndex = i;
                        break;
                    }
                }
            }
    
            // 如果没有找到感染人数所在的列，抛出异常
            if (infectionColumnIndex == -1) {
                throw new IllegalArgumentException("感染人数列 'I' 未在表头中找到");
            }
    
            // 读取每一行并计算感染人数总和
            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvSplitBy);
                if (infectionColumnIndex >= 0 && infectionColumnIndex < values.length) { // 确保列索引有效
                    try {
                        // 解析感染人数并累加
                        double infections = Double.parseDouble(values[infectionColumnIndex]);
                        totalInfections += infections;
                    } catch (NumberFormatException e) {
                        // 如果某行的感染人数值无法解析，跳过该行并打印错误
                        System.err.println("无法解析感染人数: " + values[infectionColumnIndex] + " 在行: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
        return totalInfections;
    }

 @Override
    public Map<String, Object> getMADDPGRiskPoints(SimulationRequest request) {
        Map<String, Object> response = new HashMap<>();
        String userId = request.getuserId();
        String simulationCity = request.getCity();
        int simulationDay = request.getSimulationDay();
        int simulationHour = request.getSimulationHour();
        int thresholdInfected = request.getThresholdInfected();
        String simulationFileName = request.getSimulationFileName();

        if (simulationFileName == null || simulationFileName.isEmpty()) {
            simulationFileName = "latestRecord";
        }

        String baseDir =  ROOT_FILE_PATH+userId + "\\" + "SimulationResult" +  "\\"+"MADDPG_result\\"+simulationCity + "\\";
        if ("latestRecord".equals(simulationFileName)) {
            Integer curId = lockSimulationRecordMapper.getLatestSimulationId(simulationCity,userId);
            if (curId == null) {
                response.put("msg", "没有最新的模拟记录");
                return response;
            }
            String queryFileName = lockSimulationRecordMapper.getFilePathById(curId);
            baseDir += queryFileName + "\\";
        } else {
            baseDir += simulationFileName + "\\";
        }

        simulationHour = (simulationDay - 1) * 24 + (simulationHour - 1);
        String filePath = baseDir + "simulation_DSIHR_result_" + simulationHour + ".npy";

        File file = new File(filePath);
        if (!file.exists()) {
            response.put("msg", "没有这个时刻的模拟结果");
            return response;
        }

        // 读取 .npy 文件
        INDArray simulationResult;
        try {
            simulationResult = Nd4j.createFromNpyFile(file);
        } catch (Exception e) {
            response.put("msg", "读取 .npy 文件失败");
            return response;
        }

        INDArray iDataArray = simulationResult.getRow(2);
        List<List<Object>> result = new ArrayList<>();
        for (int i = 0; i < iDataArray.columns(); i++) {
            int infectedCount = iDataArray.getInt(i);
            if (infectedCount >= thresholdInfected) {
                result.add(Arrays.asList(i, infectedCount));
            }
        }

        if (result.isEmpty()) {
            response.put("msg", "没有超过阈值的网格");
        } else {
            response.put("msg", "success");
        }
        response.put("result", result);
        return response;
    }






}