package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.service.IUnlockSimulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.domain.AjaxResult;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/infection")
public class UnlockSimulationController {

    @Autowired
    private IUnlockSimulationService unlockSimulationService;

    @PostMapping("/inquireCitySimulationResult")
    public AjaxResult inquireCitySimulationResult() {
        List<Map<String, Object>> citySimulationResult = unlockSimulationService.getCitySimulationResult();
        Map<String, Object> response = new HashMap<>();
        response.put("msg", "success");
        response.put("simulation_task", citySimulationResult);
        return AjaxResult.success(response);
    }
}
