package com.ruoyi.web;
import com.ruoyi.infection.service.ILockSimulationTimeRecordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
@SpringBootTest
public class LockSimulationTest {

    @Autowired
    private ILockSimulationTimeRecordService lockSimulationTimeRecordService;

    @Test
    public void testAddLockSimulationRecord() {
       /*/ // 测试数据
        String testCity = "shanghai";
        String testStartTime = "2024-11-16T10:00:00";
        String userId = "1";
        // 调用服务层方法添加记录
        lockSimulationTimeRecordService.addLockSimulationTimeRecord(testCity, testStartTime,userId);

        // 可能需要检验插入结果的验证步骤（可以选择性添加，例如查询数据库或查看日志输出）
        System.out.println("添加记录的城市: " + testCity + " 开始的时间: " + testStartTime);*/
           // 准备请求参数
        /*Map<String, String> requestBody = new HashMap<>();
        requestBody.put("simulation_city", "shanghai");
        //requestBody.put("lock_simulation_time", "2024-12-01_12_00_00");
        //requestBody.put("MADDPG_simulation_time", "2024-12-01_12_00_00");
        requestBody.put("user_id", "1");
        // 调用控制器接口
        Map<String, Object> responseBody = lockSimulationTimeRecordService.getLockAndMaddpgSimulationTime(requestBody);

        System.out.println("测试返回数据：" + responseBody);*/
    }
}