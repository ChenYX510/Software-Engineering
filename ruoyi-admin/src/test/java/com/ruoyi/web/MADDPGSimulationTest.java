package com.ruoyi.web;

import com.ruoyi.infection.domain.SimulationTask;
import com.ruoyi.infection.service.ISimulationTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;

import java.util.List;

@SpringBootTest
public class MADDPGSimulationTest {
    @Autowired
    private ISimulationTaskService simulationTaskService;

    @Test
    public void testRunSimulation(){
        // 创建 SimulationTask 对象并设置参数
        SimulationTask request = new SimulationTask();
        request.setSimulationFileName("2024-12-14_00_42_14");
        request.setSimulationCity("chongqing");
        request.setUserId("1");

        // 调用服务层方法运行模拟任务
        Map<String, Object> result = simulationTaskService.MADDPGSimulationTask(request);


        // 输出返回结果
        System.out.println(result);
    }
}
