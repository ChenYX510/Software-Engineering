package com.ruoyi.web.controller.infection;

import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.service.IUnlockSimulationService;
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
@RequestMapping("/infection/simulation")
public class UnlockSimulationController {

    private final IUnlockSimulationService unlockSimulationService;

    @Autowired
    public UnlockSimulationController(IUnlockSimulationService unlockSimulationService) {
        this.unlockSimulationService = unlockSimulationService;
    }

    @GetMapping("/results")
    public List<UnlockSimulation> getSimulationResultsByUserId(@RequestParam("userId") Long userId) {
        return unlockSimulationService.getSimulationResultsByUserId(userId);
    }

    @GetMapping("/tasks")
    public String inquireCitySimulationResult(@RequestParam("userId") Long userId) {
        return unlockSimulationService.inquireCitySimulationResult(userId);
    }
}
