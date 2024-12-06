package com.ruoyi.web.controller.infection;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.infection.domain.SimulationTask;
import com.ruoyi.infection.service.ISimulationTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

public class SimulationTaskController {
    @Autowired
    private ISimulationTaskService simulationTaskService;

    @PostMapping("/grid_simulation")
    public AjaxResult simulate(@RequestBody SimulationTask request) {
        try {
            Map<String, Object> result = simulationTaskService.unlockSimulationTask(request);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("Simulation failed: " + e.getMessage());
        }
    }

    @PostMapping("/lock_simulation")
    public AjaxResult lockSimulate(@RequestBody SimulationTask request) {
        try {
            Map<String, Object> result = simulationTaskService.lockSimulationTask(request);
            return AjaxResult.success(result);
        } catch (Exception e) {
            return AjaxResult.error("Simulation failed: " + e.getMessage());
        }
    }
}
