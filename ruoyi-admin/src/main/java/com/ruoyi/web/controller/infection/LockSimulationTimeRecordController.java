package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
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
@RequestMapping("/lockSimulation")
public class LockSimulationTimeRecordController {

    @Autowired
    private ILockSimulationTimeRecordService lockSimulationTimeRecordService;

    @PostMapping("/getLockSimulationStartTime")
    public String getLockSimulationStartTime(@RequestBody Map<String, String> requestBody) {
        String city = requestBody.get("city");
        String startTime = requestBody.get("start_time");
        lockSimulationTimeRecordService.addLockSimulationTimeRecord(city, startTime);
        return "{\"msg\": \"success\"}";
    }
}