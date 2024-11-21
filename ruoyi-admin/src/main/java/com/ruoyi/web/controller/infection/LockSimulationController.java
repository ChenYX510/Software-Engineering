package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.service.ILockSimulationService;
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
@RequestMapping("/api/lockSimulation")
public class LockSimulationController {

    @Autowired
    private ILockSimulationService lockSimulationService;

    @PostMapping("/getLockEveryHourInfection")
    public Object getLockEveryHourInfection(@RequestParam String city,
                                            @RequestParam(required = false, defaultValue = "latestRecord") String simulationFileName) {
        List<Double> result = lockSimulationService.getLockEveryHourInfection(city, simulationFileName);
        if (result == null) {
            return "没有最新的模拟记录";
        }
        return result;
    }
    @PostMapping("/get_every_hour_infection")
    public Object getEveryHourInfection(@RequestParam String city,
                                            @RequestParam(required = false, defaultValue = "latestRecord") String simulationFileName) {
        List<Double> result = lockSimulationService.getEveryHourInfection(city, simulationFileName);
        if (result == null) {
            return "没有最新的模拟记录";
        }
        return result;
    }
}
