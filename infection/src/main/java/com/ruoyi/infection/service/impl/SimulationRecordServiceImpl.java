package com.ruoyi.infection.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ruoyi.infection.domain.SimulationRecord;
import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.mapper.SimulationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.infection.service.ISimulationRecordService;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

@Service
public class SimulationRecordServiceImpl implements ISimulationRecordService {

    @Autowired
    private SimulationRecordMapper simulationRecordMapper;

    @Override
    public List<Long> getIdsByCity(String city) {
        return simulationRecordMapper.selectIdsByCity(city);
    }
}
