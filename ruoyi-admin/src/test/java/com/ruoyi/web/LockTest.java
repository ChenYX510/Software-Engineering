package com.ruoyi.web;
import com.ruoyi.infection.service.ILockSimulationService;
import org.junit.jupiter.api.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;

import java.util.List;
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
        /*try {
            // 加载 .npy 文件
            File npyFile = new File("C:\\Users\\86182\\Desktop\\数据库课设\\Software-Engineering\\ruoyi-admin\\testuser\\1\\SimulationResult\\MADDPG_result\\chongqing\\test\\simulation_DSIHR_result_0.npy");
            INDArray array = Nd4j.createFromNpyFile(npyFile);

            // 打印数组信息
            System.out.println("Array shape: " + array.shapeInfoToString());
            System.out.println("Array data: " + array);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}