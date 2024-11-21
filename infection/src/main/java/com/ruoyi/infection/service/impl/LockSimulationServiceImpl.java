package com.ruoyi.infection.service.impl;

import com.alibaba.fastjson2.support.csv.CSVReader;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.mapper.LockSimulationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ILockSimulationService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.nio.file.Files;

@Service
public class LockSimulationServiceImpl implements ILockSimulationService {

    @Autowired
    private LockSimulationRecordMapper lockSimulationRecordMapper;

    @Override
    public List<Double> getLockEveryHourInfection(String city, String simulationFileName) {
        String dir = "./GuangZhou_simulation/lock_result/" + city + "/";
        if (Objects.equals(simulationFileName, "latestRecord")) {
            List<Long> ids = lockSimulationRecordMapper.selectIdsByCity(city);
            Long maxId = ids.stream().max(Long::compare).orElse(-1L);

            if (maxId == -1) {
                return null; // No records
            }

            String queryFilePath = lockSimulationRecordMapper.selectFilepathById(maxId);
            dir = Paths.get(dir, queryFilePath).toString();
        } else {
            dir = Paths.get(dir, simulationFileName).toString();
        }

        // Load data from CSV files
        List<Double> result = new ArrayList<>();// 用于存储每个文件中 "I" 列的总和
        int curHour = 0;
        while (new File(dir + "/SIHR_" + curHour + ".csv").exists()) {
            double sum = loadAndSumInfections(dir + "/SIHR_" + curHour + ".csv");
            result.add(sum*0.8 );
            curHour++;
        }

        return result;
    }

    @Override
    public List<Double> getEveryHourInfection(String city, String simulationFileName) {
        String dir = "./GuangZhou_simulation/result/" + city + "/";
        if (Objects.equals(simulationFileName, "latestRecord")) {
            List<Long> ids = lockSimulationRecordMapper.selectIdByCity(city);
            Long maxId = ids.stream().max(Long::compare).orElse(-1L);

            if (maxId == -1) {
                return null; // No records
            }

            String queryFilePath = lockSimulationRecordMapper.selectFilespathById(maxId);
            dir = Paths.get(dir, queryFilePath).toString();//这个地方还有点疑问
        } else {
            dir = Paths.get(dir, simulationFileName).toString();
        }

        // Load data from CSV files
        List<Double> result = new ArrayList<>();
        int curHour = 0;
        while (new File(dir + "/SIHR_" + curHour + ".csv").exists()) {
            double sum = loadAndSumInfections(dir + "/SIHR_" + curHour + ".csv");
            result.add(sum );
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
    
}