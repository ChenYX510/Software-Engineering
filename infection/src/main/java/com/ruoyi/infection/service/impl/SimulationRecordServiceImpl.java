package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.SimulationcityRecord;
import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.mapper.SimulationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ISimulationRecordService;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Long> getIdsByCity(String city) {
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

            List<SimulationcityRecord> simulationRecords = simulationRecordMapper.selectSimulationRecordsByCity(city);

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

            List<SimulationcityRecord> simulationRecords = simulationRecordMapper.selectSimulationLockRecordsByCity(city);

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

            List<SimulationcityRecord> simulationRecords = simulationRecordMapper.selectSimulationMADDPGRecordsByCity(city);

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
}
