package com.ruoyi.web;

import com.ruoyi.infection.domain.SimulationTask;
import com.ruoyi.infection.service.ISimulationTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Map;

import java.util.List;

@SpringBootTest
public class SimulationTest {
    @Autowired
    private ISimulationTaskService simulationTaskService;

    @Test
    public void testRunSimulation(){
        // 创建 SimulationTask 对象并设置参数
        SimulationTask request = new SimulationTask();
        request.setR0(2.5);
        request.setI_H_para(0.2);
        request.setI_R_para(0.1);
        request.setH_R_para(0.05);
        request.setI_input("{\"0\": 9, \"1\": 6}");
        request.setRegionList("{\"0\": [113.373708, 23.12986], \"1\": [113.298808, 23.08986]}");
        request.setSimulationDays(1);
        request.setSimulationCity("chongqing");

        // 调用服务层方法运行模拟任务
        Map<String, Object> result = simulationTaskService.unlockSimulationTask(request);


        // 输出返回结果
        System.out.println(result);
    }
}
