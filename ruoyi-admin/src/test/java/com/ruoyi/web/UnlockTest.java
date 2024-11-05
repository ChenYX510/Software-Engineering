package com.ruoyi.web;

import com.ruoyi.infection.domain.UnlockSimulation;
import com.ruoyi.infection.service.IUnlockSimulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

@SpringBootTest
public class UnlockTest {
    @Autowired
    private IUnlockSimulationService unlockSimulationService;

    @Test
    public void testGetSimulationRecords(){
        // 使用一个测试的 user_id
        Long testUserId = 1L;

/*        // 调用服务层方法查询模拟结果
        List<UnlockSimulation> results = unlockSimulationService.getSimulationResultsByUserId(testUserId);

        // 输出查询结果
        if (results != null && !results.isEmpty()) {
            for (UnlockSimulation result : results) {
                System.out.println(result);
            }
        } else {
            System.out.println("No results found for user_id: " + testUserId);
        }*/

        String result = unlockSimulationService.inquireCitySimulationResult(testUserId);
        System.out.println(result);
    }
}
