package com.ruoyi.web;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.List;
@SpringBootTest
public class LockSimulationTest {

    @Autowired
    private ILockSimulationTimeRecordService lockSimulationTimeRecordService;

    @Test
    public void testAddLockSimulationRecord() {
        // 测试数据
        String testCity = "TestCity";
        String testStartTime = "2024-11-16T10:00:00";

        // 调用服务层方法添加记录
        lockSimulationTimeRecordService.addLockSimulationTimeRecord(testCity, testStartTime);

        // 可能需要检验插入结果的验证步骤（可以选择性添加，例如查询数据库或查看日志输出）
        System.out.println("Record added for city: " + testCity + " with start time: " + testStartTime);
    }
}