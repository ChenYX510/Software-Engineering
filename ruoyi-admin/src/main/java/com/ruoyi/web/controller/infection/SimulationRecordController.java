package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.domain.SimulationRecord;
import com.ruoyi.infection.service.ISimulationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/simulation")
public class SimulationRecordController {
    @Autowired
    private ISimulationRecordService simulationRecordService;

    @PostMapping("/test_database")
    public Map<String, Object> testDatabase() {
        String city = "ezhou"; // 将城市硬编码为 'ezhou'
        List<Long> curId = simulationRecordService.getIdsByCity(city);

        Map<String, Object> response = new HashMap<>();
        response.put("msg", "succeed");
        response.put("msg1", curId.toString());
        response.put("msg2", curId.isEmpty() ? "No data" : curId.get(0).toString());
        response.put("msg2_1", curId.isEmpty() ? "No data" : curId.get(0).toString());
        response.put("msg3", curId.getClass().getName());
        response.put("msg4", curId.isEmpty() ? "No data" : curId.get(0).getClass().getName());
        response.put("msg4_1", curId.isEmpty() ? "No data" : curId.get(0).getClass().getName());

        return response;
    }
}
