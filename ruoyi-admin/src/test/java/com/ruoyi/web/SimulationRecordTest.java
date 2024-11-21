package com.ruoyi.web;

import com.ruoyi.infection.domain.SimulationRecord;
import com.ruoyi.infection.service.ISimulationRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;

@SpringBootTest
public class SimulationRecordTest {
    @Autowired
    private ISimulationRecordService simulationRecordService; // 注入服务

    @Test
    public void testGetSimulationRecordsByCity() {
        // 使用一个测试的城市名称
        String testCity = "ezhou";  // 假设你需要查询的城市为 "ezhou"

        // 调用服务层方法查询结果
        List<Long> resultIds = simulationRecordService.getIdsByCity(testCity);

        // 输出查询结果
        if (resultIds != null && !resultIds.isEmpty()) {
            for (Long id : resultIds) {
                System.out.println("Found simulation record with id: " + id);
            }
        } else {
            System.out.println("No simulation records found for city: " + testCity);
        }
    }
}
