package com.ruoyi.web;
import com.ruoyi.infection.service.ILockSimulationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
@SpringBootTest
public class GetInfectionTest {

    @Autowired
    private ILockSimulationService lockSimulationService;

    @Test
    public void testGetLockEveryHourInfection() {
        String testCity = "Guangzhou";
        String testSimulationFileName = "latestRecord"; // 或者指定一个测试文件名

        // 调用服务层方法查询感染结果
        List<Double> results = lockSimulationService.getEveryHourInfection(testCity, testSimulationFileName);

        // 输出查询结果
        if (results != null && !results.isEmpty()) {
            System.out.println("Infection results for city: " + testCity);
            for (int i = 0; i < results.size(); i++) {
                System.out.println("Hour " + i + ": " + results.get(i));
            }
        } else {
            System.out.println("No results found for city: " + testCity);
        }
    }
}