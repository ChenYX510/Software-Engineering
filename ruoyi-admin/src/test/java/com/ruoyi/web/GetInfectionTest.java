package com.ruoyi.web;
import com.ruoyi.infection.domain.SimulationRequest;
import com.ruoyi.infection.service.ILockSimulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
@SpringBootTest
public class GetInfectionTest {

    @Autowired
    private ILockSimulationService lockSimulationService;

    @Test
    public void testGetMADDPGSimulationRiskPoint() {
       /*/ // 模拟请求参数
        SimulationRequest request = new SimulationRequest();
        request.setCity("Guangzhou");
        request.setSimulationDay(1);
        request.setSimulationHour(10);
        request.setThresholdInfected(50);
        request.setSimulationFileName("latestRecord");

         Map<String, Object> response = lockSimulationService.getMADDPGRiskPoints(request);

         System.out.println( response);*/
    }
}