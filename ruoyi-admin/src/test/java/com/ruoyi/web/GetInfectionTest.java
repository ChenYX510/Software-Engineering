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
        // 模拟请求参数
String city="shanghai";
String userId="1";
String filepath="latestRecord";

List<Double>  response = lockSimulationService.getLockEveryHourInfection(city,userId,filepath);

         System.out.println( response);
    }
}