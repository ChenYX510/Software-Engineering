package com.ruoyi.web;
import com.ruoyi.infection.service.ILockSimulationService;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import com.ruoyi.infection.domain.SimulationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;

import java.util.List;
import java.util.Map;
@SpringBootTest
public class LockTest {

    @Autowired
    private ILockSimulationService lockSimulationService;

    @Test
    public void testGetLockEveryHourInfection() {
        /*String testCity = "chongqing";
        String userId = "1"; // 或者指定一个测试文件名
        String testSimulationFileName = "test"; // 或者指定一个测试文件名

        // 调用服务层方法查询感染结果
        List<Double> results = lockSimulationService.getMADDPGEveryHourInfection(testCity, userId,testSimulationFileName);

        // 输出查询结果
        if (results != null && !results.isEmpty()) {
            System.out.println("该城市的感染数据: " + testCity);
            for (int i = 0; i < results.size(); i++) {
                System.out.println("Hour " + i + ": " + results.get(i));
            }
        } else {
            System.out.println("没有该城市的查询结果：" + testCity);
        }*/
        /*
        String userId="1";
         String city="chongqing";
         Integer simulationDay=1;
         Integer simulationHour=1;
         Integer thresholdInfected=-1;
         String simulationFileName="test";

         SimulationRequest request = new SimulationRequest();
         request.setuserId(userId);
         request.setCity(city);
         request.setSimulationDay(simulationDay);
         request.setSimulationHour(simulationHour);
         request.setThresholdInfected(thresholdInfected);
         request.setSimulationFileName(simulationFileName);
         Map<String, Object> response = lockSimulationService.getMADDPGRiskPoints(request);
         System.out.println(response);*/
    }
}