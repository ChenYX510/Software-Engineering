package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
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
    private static final String ROOT_DIR = "./GuangZhou_simulation/result/";

    @Autowired
    private UnlockInfectionMapper unlockInfectionMapper;

    @Override
    public List<Map<String, Object>> getCitySimulationResult() {
        List<String> cityNames = Arrays.asList("shanghai", "chongqing", "guangzhou", "wulumuqi", "ningbo", "dongying", "weihai", "zibo", "lianyungang", "wuxi", "ezhou", "sihui");
        List<Map<String, Object>> cityResults = new ArrayList<>();

        for (String city : cityNames) {
            String cityDir = ROOT_DIR + city;
            File directory = new File(cityDir);
            Map<String, Object> cityData = new HashMap<>();
            int numRecords = directory.exists() ? Objects.requireNonNull(directory.list()).length : 0;

            if (numRecords == 0) {
                cityData.put("city", city);
                cityData.put("simulation_record_num", 0);
            } else {
                List<Map<String, Object>> recordDetails = new ArrayList<>();
                List<UnlockSimulation> records = unlockInfectionMapper.getSimulationRecords(city);

                for (UnlockSimulation record : records) {
                    String recordDir = cityDir + "/" + record.getFilepath() + "/";
                    Map<String, Object> simulationItem = new HashMap<>();
                    simulationItem.put("simulation_time", record.getFilepath());
                    simulationItem.put("task_state", record.getState());
                    File jsonFile = new File(recordDir + "data.json");

                    if (jsonFile.exists()) {
                        try (FileReader reader = new FileReader(jsonFile)) {
                            simulationItem.put("para_json", new Gson().fromJson(reader, Map.class));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    recordDetails.add(simulationItem);
                }
                cityData.put("city", city);
                cityData.put("simulation_record_num", numRecords);
                cityData.put("simulation_record", recordDetails);
            }
            cityResults.add(cityData);
        }

        return cityResults;
    }
}
