package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.mapper.UnlockInfectionMapper;
import com.ruoyi.infection.service.IUnlockSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Service
public class UnlockSimulationServiceImpl implements IUnlockSimulationService {
    private static final String ROOT_FILE_PATH = System.getProperty("user.dir") + "\\testUser\\";
    private final UnlockInfectionMapper unlockInfectionMapper;
    private final Gson gson = new Gson();

    @Autowired
    public UnlockSimulationServiceImpl(UnlockInfectionMapper unlockInfectionMapper) {
        this.unlockInfectionMapper = unlockInfectionMapper;
    }

    @Override
    public List<UnlockSimulation> getSimulationResultsByUserId(Long userId) {
        return unlockInfectionMapper.selectSimulationResultsByUserId(userId);
    }

    @Override
    public String inquireCitySimulationResult(Long userId) {
        Map<String, Object> dataJson = new HashMap<>();
        Map<String, Object> cityJson = new HashMap<>();
        List<Map<String, Object>> jsonFileList = new ArrayList<>();
        List<UnlockSimulation> resultList = unlockInfectionMapper.selectSimulationResultsByUserId(userId);

        for (UnlockSimulation resultItem : resultList) {
            String cityName = resultItem.getCityName();
            cityJson.put("city", cityName);
            String fileDirPath = ROOT_FILE_PATH + userId + "\\SimulationResult\\unlock_result\\" + cityName;
            File dataFile = new File(fileDirPath + "\\data.json");
            System.out.println(fileDirPath);
            JsonObject paraJson = new JsonObject();

            if (dataFile.exists()) {
                try (FileReader reader = new FileReader(dataFile)) {
                    paraJson = gson.fromJson(reader, JsonObject.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Map<String, Object> simulationItem = new HashMap<>();
            simulationItem.put("simulation_time", resultItem.getFilepath());
            simulationItem.put("task_state", resultItem.getState());
            simulationItem.put("para_json", paraJson);
            jsonFileList.add(simulationItem);
        }

        cityJson.put("simulation_record", jsonFileList);
        dataJson.put("msg", "success");
        dataJson.put("simulation_task", cityJson);

        return gson.toJson(dataJson);
    }
}
