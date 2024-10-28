package com.ruoyi.web;

import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.service.IUnlockSimulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UnlockTest {
    @Autowired
    private IUnlockSimulationService unlockSimulationService;

    @Test
    public void testGetSimulationRecords(){
        UnlockSimulation unlockSimulation = new UnlockSimulation();
        System.out.println(unlockSimulationService.getCitySimulationResult());
    }
}
