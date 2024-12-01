package com.ruoyi.web;

import com.ruoyi.infection.domain.CitySimulationResult;
import com.ruoyi.infection.domain.SimulationRecord;
import com.ruoyi.infection.service.ISimulationRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

@SpringBootTest
public class SimulationRecordTest {
    @Autowired
    private ISimulationRecordService simulationRecordService; // 注入服务

    @Test
    public void testGetSimulationRecordsByCity() {
       /*/ // 使用一个测试的城市名称
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
        }*/

        String userId="1";
               // 调用服务层方法查询所有城市的模拟结果
        List<CitySimulationResult> results = simulationRecordService.getCitySimulationResults(userId);

        // 断言结果不为空
        assertNotNull(results, "结果不为空");

        // 断言结果列表非空
        assertFalse(results.isEmpty(), "结果列表非空");

        // 输出查询结果
        for (CitySimulationResult result : results) {
            System.out.println("城市: " + result.getCity());
            System.out.println("模拟结果数量： " + result.getSimulationRecordNum());

            if (result.getSimulationRecord() != null && !result.getSimulationRecord().isEmpty()) {
                result.getSimulationRecord().forEach(record -> {
                    System.out.println("  Simulation Time: " + record.getSimulationTime());
                    System.out.println("  Task State: " + record.getTaskState());
                    System.out.println("  Para JSON: " + record.getParaJson());
                });
            } else {
                System.out.println("  这个城市没有模拟记录");
            }
        }
    }
}
