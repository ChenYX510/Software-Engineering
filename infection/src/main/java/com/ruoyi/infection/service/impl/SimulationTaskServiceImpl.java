package com.ruoyi.infection.service.impl;

import com.ruoyi.infection.domain.SimulationTask;
import com.ruoyi.infection.service.ISimulationTaskService;
import com.ruoyi.infection.mapper.SimulationTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.io.FileWriter;
import java.io.BufferedWriter; // 写入文件
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.locationtech.jts.geom.Geometry;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.geotools.feature.FeatureIterator;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


@Service
public class SimulationTaskServiceImpl implements ISimulationTaskService {
    @Autowired
    private SimulationTaskMapper simulationTaskMapper;
    private static final String ROOT_FILE_PATH = System.getProperty("user.dir") + "\\testUser\\";

    private static final Logger logger = Logger.getLogger(SimulationTaskServiceImpl.class.getName());
    private final ExecutorService executor = Executors.newFixedThreadPool(2); // 线程池
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> unlockSimulationTask(SimulationTask request) {
        String simulationCity = request.getSimulationCity();

        double R0 = request.getR0();
        double I_H_para = request.getI_H_para();
        double I_R_para = request.getI_R_para();
        double H_R_para = request.getH_R_para();
        String I_input = request.getI_input();
        String region_list = request.getRegionList();
        int simulation_days = request.getSimulationDays();;
        String simulation_city = request.getSimulationCity();

        // 移除转义字符，得到有效的 JSON 格式
        I_input = I_input.replace("\\", "\\\\");

        System.out.println("I_input: " + I_input);

        // 检查所需文件是否存在
        String msg = "start simulate";
        System.out.println(ROOT_FILE_PATH  + "1\\" + simulationCity + "\\city.shp");
        if (!new File(ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\city.shp").exists()) {
            msg = "缺少网格文件";
        } else if (!new File(ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\population.npy").exists()) {
            msg = "缺少人口文件";
        } /*else if (!new File(ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\OD.npy").exists()) {
            msg = "缺少OD数据文件";
        }*/

        Map<String, Object> result = new HashMap<>();
        String curDirName = "";
        if ("start simulate".equals(msg)) {
            curDirName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss"));
            // 在数据库创建一条新的模拟记录
            boolean dbSaveStatus = saveRecordDatabase("none_type", curDirName, simulationCity);
            if (dbSaveStatus) {
                logger.info("Database record saved successfully for simulation: " + simulationCity);
            } else {
                logger.warning("Failed to save database record for simulation: " + simulationCity);
            }
            // 启动模拟任务
            //startSimulationTask(request);
            //executor.submit(() -> runSimulationTask(request));
            // result.put("status", true);
            // 调用 Python 脚本
            boolean pythonExecutionStatus = executePythonScript(
                    ROOT_FILE_PATH + "simulate.py", // 替换为 Python 脚本路径
                    R0, I_H_para, I_R_para, H_R_para, I_input, region_list, simulation_days, simulation_city, curDirName
            );

            if (pythonExecutionStatus) {
                result.put("status", true);
                msg = "Simulation started successfully";
            } else {
                result.put("status", false);
                msg = "Failed to start simulation";
                result.put("msg", msg);
                return result;
            }
        } else {
            result.put("status", false);
            result.put("msg", msg);
            return result;
        }
        // 更新数据库
        boolean dbUpdateStatus = modifyStatus("none_type",curDirName);
        if (dbUpdateStatus) {
            logger.info("Database status updated successfully for simulation: " + simulationCity);
        } else {
            logger.warning("Failed to update database status for simulation: " + simulationCity);
        }
        result.put("msg", msg);
        return result;
    }

    // 调用python脚本的具体实现
    private boolean executePythonScript(
            String scriptPath,
            double R0,
            double I_H_para,
            double I_R_para,
            double H_R_para,
            String I_input,
            String regionList,
            int simulationDays,
            String simulationCity,
            String curDirName) {

        // 指定虚拟环境的 Python 解释器路径
        String pythonExecutable = "C:\\Users\\Lenovo\\anaconda3\\envs\\myenv39\\python.exe"; // 替换为你的虚拟环境路径

        try {
            // 构建命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                    pythonExecutable, scriptPath, // 使用虚拟环境的 Python 解释器
                    "--R0", String.valueOf(R0),
                    "--I_H_para", String.valueOf(I_H_para),
                    "--I_R_para", String.valueOf(I_R_para),
                    "--H_R_para", String.valueOf(H_R_para),
                    // 注意这个传参形式，必须要双斜杠才能保证json中的双引号传到python
                    "--I_input", "\"" + I_input.replace("\"", "\\\"") + "\"", // 通过replace确保转义双引号
                    "--region_list", "\"" + regionList.replace("\"", "\\\"") + "\"",
                    "--simulation_days", String.valueOf(simulationDays),
                    "--simulation_city", simulationCity,
                    "--cur_dir_name", curDirName
            );


            // 设置环境变量和工作目录
            processBuilder.directory(new File(ROOT_FILE_PATH+ "\\1\\"));
            processBuilder.redirectErrorStream(true);


            // 启动进程
            Process process = processBuilder.start();

            // 捕获输出
            try (Scanner scanner = new Scanner(process.getInputStream())) {
                while (scanner.hasNextLine()) {
                    logger.info(scanner.nextLine());
                }
            }

            // 等待脚本执行完成
            int exitCode = process.waitFor();
            return exitCode == 0; // 返回是否成功
        } catch (Exception e) {
            logger.severe("Error executing Python script: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 在数据库中新插入一条模拟记录
    public boolean saveRecordDatabase(String funcType, String dirName, String cityName) {
        String tableName;
        switch (funcType) {
            case "none_type":
                tableName = "infection_unlock_simulation_result";
                break;
            case "lock_type":
                tableName = "infection_lock_simulation_result";
                break;
            case "MADDPG_type":
                tableName = "MADDPG_simulationRecord";
                break;
            default:
                logger.warning("Invalid funcType provided: " + funcType);
                return false;
        }

        // 获取当前最大 ID
        Integer currentMaxId = simulationTaskMapper.getMaxId(tableName);
        int newId = (currentMaxId == null ? 0 : currentMaxId) + 1;

        // 插入新记录
        int rowsInserted = simulationTaskMapper.insertSimulationRecord(tableName, newId, dirName, cityName, "False");
        if (rowsInserted > 0) {
            logger.info("Successfully inserted record into " + tableName + " with ID: " + newId);
            return true;
        } else {
            logger.warning("Failed to insert record into " + tableName);
            return false;
        }
    }

    // 在数据库中更新模拟状态
    public boolean modifyStatus(String funcType, String dirName) {
        String tableName;

        // 根据类型选择对应表名
        switch (funcType) {
            case "none_type":
                tableName = "simulationRecord";
                break;
            case "lock_type":
                tableName = "lock_simulationRecord";
                break;
            case "MADDPG_type":
                tableName = "MADDPG_simulationRecord";
                break;
            default:
                logger.warning("Invalid funcType provided: " + funcType);
                return false;
        }

        // 调用 Mapper 层更新状态
        int rows = simulationTaskMapper.updateTaskStatus(tableName, "True", dirName);
        if (rows > 0) {
            logger.info("Successfully updated state for dirName: " + dirName + " in table: " + tableName);
            return true;
        } else {
            logger.warning("No rows updated for dirName: " + dirName + " in table: " + tableName);
            return false;
        }
    }












    private void startSimulationTask(SimulationTask request) {
        String simulationCity = request.getSimulationCity();
        String curDirName = new Date().toString().replace(":", "_").replace(" ", "_");
        String resultPath = "./GuangZhou_simulation/result/" + simulationCity + "/" + curDirName;

        // 创建目录
        new File(resultPath).mkdirs();

        logger.info("------------- simulationTask start -------------");

        // 保存模拟参数
        Map<String, Object> data = new HashMap<>();
        data.put("R0", request.getR0());
        data.put("I_H_para", request.getI_H_para());
        data.put("I_R_para", request.getI_R_para());
        data.put("H_R_para", request.getH_R_para());
        data.put("I_input", request.getI_input());
        data.put("region_list", request.getRegionList());
        data.put("simulation_days", request.getSimulationDays());
        data.put("simulation_city", simulationCity);

        try (FileWriter writer = new FileWriter(resultPath + "/data.json")) {
            writer.write(new com.google.gson.Gson().toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }


        // 读取网格索引字典
        String jsonFilePath = ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\grid_index_dic.json";
        Map<String, Integer> gridIndexDict = loadGridIndexDict(jsonFilePath);

        if (gridIndexDict.isEmpty()) {
            logger.warning("Failed to load grid index dictionary for city: " + simulationCity);
        } else {
            logger.info("Successfully loaded grid index dictionary for city: " + simulationCity);
            //System.out.println("Grid Index Dict: " + gridIndexDict);
        }

        // 读取网格 SHP 文件
        String shpFilePath =ROOT_FILE_PATH  + "\\1\\" + simulationCity + "\\city.shp";
        Geometry[] gridArray = loadShpFile(shpFilePath);
        if (gridArray == null) {
            System.out.println("SHP file loading failed.");
        } else {
            System.out.println("Geometry array loaded with size: " + gridArray.length);
            // 打印查看gridArray的内容
            /*for (int i = 0; i < gridArray.length; i++) {
                System.out.println("Geometry at index " + i + ": " + gridArray[i]);
            }*/
        }

        // 读取人口数据（模拟处理）
        double[] N_0 = loadPopulationData(simulationCity);
        double[] S_0 = Arrays.copyOf(N_0, N_0.length);
        double[] I_0 = new double[N_0.length];
        double[] H_0 = new double[N_0.length];
        double[] R_0 = new double[N_0.length];
        int gridLength = S_0.length;

        logger.info("grid_length: " + gridLength);
        //logger.info("N_0: " + Arrays.toString(N_0));

        // 解析 I_input 和 region_list 并进行处理
        String I_input = request.getI_input();
        String regionList = request.getRegionList();
        processInputAndRegion(I_input, regionList, gridIndexDict, simulationCity, S_0, I_0);
        logger.info("S_0: " + Arrays.toString(S_0));

        //String outputDir =
        //runSimulation(request.getSimulationCity(),request.getSimulationDays(),N_0,S_0,I_0,H_0,R_0,);

    }

    // 读网格数据的json文件
    public Map<String, Integer> loadGridIndexDict(String filePath) {
        Map<String, Integer> gridIndexDict = null;
        System.out.println("start read json");

        try {
            // 使用 Java 8 方式读取文件内容
            byte[] jsonBytes = Files.readAllBytes(Paths.get(filePath));
            String jsonContent = new String(jsonBytes, "UTF-8"); // 指定字符编码

            // 使用 Jackson ObjectMapper 解析 JSON 文件
            ObjectMapper objectMapper = new ObjectMapper();
            gridIndexDict = objectMapper.readValue(jsonContent, new TypeReference<Map<String, Integer>>() {});

            // 打印结果确认解析成功
            //logger.info("Grid index dictionary: " + gridIndexDict);

        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Error reading or parsing the JSON file: " + e.getMessage());
        }
        return gridIndexDict;
    }

    // 读取 SHP 文件并提取 Geometry 数组
    public static Geometry[] loadShpFile(String shpFilePath) {
        if (!Files.exists(Paths.get(shpFilePath))) {
            System.out.println("------------- no shp file found at path: " + shpFilePath + " -------------");
            return null;
        }

        try {
            // 使用 GeoTools 加载 SHP 文件
            FileDataStore store = FileDataStoreFinder.getDataStore(Paths.get(shpFilePath).toFile());
            if (store == null) {
                System.out.println("Failed to load the SHP file: " + shpFilePath);
                return null;
            }

            // 获取特征源
            SimpleFeatureCollection featureCollection = store.getFeatureSource().getFeatures();
            List<Geometry> geometries = new ArrayList<>();

            // 遍历并提取 Geometry 对象
            FeatureIterator<SimpleFeature> features = featureCollection.features();
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                Object geometry = feature.getDefaultGeometry();
                if (geometry instanceof Geometry) {
                    geometries.add((Geometry) geometry);
                } else {
                    System.out.println("Invalid geometry type found: " + geometry.getClass());
                }
            }
            features.close(); // 关闭 FeatureIterator

            // 将 List 转换为数组返回
            return geometries.toArray(new Geometry[0]);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error while reading SHP file: " + e.getMessage());
        }

        return null;
    }

    // 读取人口数据文件 population.npy
    public static double[] loadPopulationData(String simulationCity) {
        String filePath = ROOT_FILE_PATH + "\\1\\" + simulationCity + "\\population.npy";
        if (!new File(filePath).exists()) {
            logger.warning("Population data file not found: " + filePath);
            return new double[0]; // 返回空数组，表示未加载到数据
        }

        try {
            // 使用 Nd4j（或其他支持 NPY 格式的库）加载人口数据
            INDArray populationArray = Nd4j.createFromNpyFile(new File(filePath));
            if (populationArray.isVector()) {
                // 将人口数据转换为 Java 的 double 数组
                double[] populationData = populationArray.toDoubleVector();
                logger.info("Successfully loaded population data with length: " + populationData.length);
                return populationData;
            } else {
                logger.warning("Population data is not in vector format!");
                return new double[0];
            }
        } catch (Exception e) {
            logger.severe("Error while reading population data file: " + e.getMessage());
            return new double[0];
        }
    }

    private void processInputAndRegion(String I_input, String regionList, Map<String, Integer> gridIndexDict,
                                       String simulationCity, double[] S_0, double[] I_0) {
        try {
            // 解析 I_input 为 Map<String, Integer> 类型
            Map<String, Integer> I_inputMap = objectMapper.readValue(I_input, new TypeReference<Map<String, Integer>>() {});
            // 将 I_inputMap 的值存入一个 List 中
            List<Integer> I_inputList = new ArrayList<>(I_inputMap.values());

            // 解析 regionList 为 Map<String, double[]> 类型
            Map<String, double[]> regionMap = objectMapper.readValue(regionList, new TypeReference<Map<String, double[]>>() {});

            // 遍历 regionList
            for (Map.Entry<String, double[]> entry : regionMap.entrySet()) {
                // 获取区域的 key
                String key = entry.getKey();
                // 获取对应的经纬度
                double[] latLon = entry.getValue();
                double lat = latLon[0];
                double lon = latLon[1];

                // 计算网格索引
                int[] gridIndex = getGridIndexFunc(lat, lon, simulationCity);
                String keyName = gridIndex[0] + "_" + gridIndex[1];

                // 如果网格索引在 gridIndexDict 中，则更新 S_0 和 I_0
                if (gridIndexDict.containsKey(keyName)) {
                    int gridPosition = gridIndexDict.get(keyName);
                    // 更新 S_0 数组：确保 S_0 不变为负数
                    S_0[gridPosition] = Math.max(0, S_0[gridPosition] - I_inputList.get(Integer.parseInt(key)));
                    // 更新 I_0 数组
                    I_0[gridPosition] += I_inputList.get(Integer.parseInt(key));
                    // 打印更新后的值
                    logger.info("Updated grid " + keyName + ": S_0 = " + S_0[gridPosition] + ", I_0 = " + I_0[gridPosition]);
                }
            }
        } catch (Exception e) {
            logger.severe("Error processing I_input or regionList: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int[] getGridIndexFunc(double lat, double lon, String simulationCity) {
        try {
            // 使用 ObjectMapper 读取 JSON 文件并解析为 Map<String, double[]>
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<Double>> cityOPoint = objectMapper.readValue(
                    new File(ROOT_FILE_PATH + "\\1\\" +"city_O_point.json"),
                    new TypeReference<Map<String, List<Double>>>() {}
            );

            if (!cityOPoint.containsKey(simulationCity)) {
                logger.warning("Simulation city not found in city_O_point.json: " + simulationCity);
                return new int[]{-1, -1}; // 返回无效值表示计算失败
            }

            // 获取城市的原点坐标
            List<Double> oPointList = cityOPoint.get(simulationCity);
            if (oPointList == null || oPointList.size() < 2) {
                logger.warning("Invalid origin point data for city: " + simulationCity);
                return new int[]{-1, -1}; // 返回无效值表示计算失败
            }

            double oLat = oPointList.get(0);
            double oLon = oPointList.get(1);

            // 计算行列索引
            double rowIndex = (lat - oLat) / 0.005;
            double colIndex = (lon - oLon) / 0.005;

            // 向下取整并返回结果
            return new int[]{(int) Math.floor(rowIndex), (int) Math.floor(colIndex)};
        } catch (Exception e) {
            logger.severe("Error while calculating grid index: " + e.getMessage());
            e.printStackTrace();
            return new int[]{-1, -1}; // 返回无效值表示计算失败
        }
    }

    public void runSimulation(String simulationCity, int simulationDays, String outputDir,
                              double[] N_0, double[] S_0, double[] I_0, double[] H_0, double[] R_0,
                              double R0, double I_H_para, double I_R_para, double H_R_para) {

        logger.info("Start simulation for city: " + simulationCity);

        int gridLength = S_0.length;

        double[][][] OD = loadODFromNpy(ROOT_FILE_PATH  + "\\1\\" + simulationCity +"\\OD.npy");

        for (int timeIndex = 0; timeIndex < simulationDays * 24; timeIndex++) {
            // Round values
            N_0 = roundArray(N_0, 4);
            S_0 = roundArray(S_0, 4);
            I_0 = roundArray(I_0, 4);
            H_0 = roundArray(H_0, 4);
            R_0 = roundArray(R_0, 4);

            // 计算移动人口
            double[] movePeople = subtractArrays(N_0, H_0);

            double[][] S_temp = new double[gridLength][1];
            double[][] I_temp = new double[gridLength][1];
            double[][] R_temp = new double[gridLength][1];

            for (int i = 0; i < gridLength; i++) {
                if (movePeople[i] != 0) {
                    I_temp[i][0] = I_0[i] / movePeople[i];
                    S_temp[i][0] = S_0[i] / movePeople[i];
                    R_temp[i][0] = R_0[i] / movePeople[i];
                } else {
                    I_temp[i][0] = 0;
                    S_temp[i][0] = 0;
                    R_temp[i][0] = 0;
                }
            }

            // Compute movement distribution
            double[][] S_people = multiplyMatrix(S_temp, OD[timeIndex]);
            double[][] I_people = multiplyMatrix(I_temp, OD[timeIndex]);
            double[][] R_people = multiplyMatrix(R_temp, OD[timeIndex]);

            S_people = roundMatrix(S_people, 4);
            I_people = roundMatrix(I_people, 4);
            R_people = roundMatrix(R_people, 4);

            // Update S, I, R values
            S_0 = updatePopulation(S_0, S_people, gridLength);
            I_0 = updatePopulation(I_0, I_people, gridLength);
            R_0 = updatePopulation(R_0, R_people, gridLength);

            // Adjust negative values
            S_0 = adjustNegativeValues(S_0);
            I_0 = adjustNegativeValues(I_0);
            R_0 = adjustNegativeValues(R_0);

            // Calculate new infections, hospitalizations, recoveries
            double[] m_infected = calculateNewInfections(R0, S_people, I_people, OD[timeIndex]);
            double[] s_infected = calculateStaticInfections(R0, S_0, I_0, N_0);
            double[] newInfected = addArrays(m_infected, s_infected);

            double[] newHospital = scaleArray(I_0, I_H_para);
            double[] newIRecovered = scaleArray(I_0, I_R_para);
            double[] newHRecovered = scaleArray(H_0, H_R_para);

            // Update final states
            S_0 = subtractArrays(S_0, newInfected);
            I_0 = addArrays(subtractArrays(addArrays(I_0, newInfected), newHospital), negateArray(newIRecovered));
            H_0 = addArrays(subtractArrays(H_0, newHRecovered), newHospital);
            R_0 = addArrays(R_0, addArrays(newIRecovered, newHRecovered));

            S_0 = adjustNegativeValues(S_0);
            I_0 = adjustNegativeValues(I_0);
            H_0 = adjustNegativeValues(H_0);
            R_0 = adjustNegativeValues(R_0);

            N_0 = addArrays(addArrays(S_0, I_0), addArrays(H_0, R_0));

            // Save results
            saveResults(outputDir, timeIndex, gridLength, S_0, I_0, H_0, R_0, newInfected, N_0);

            logger.info("Simulation step " + timeIndex + " completed.");
        }
    }

    public void runSimulationForOtherCities(String simulationCity, int simulationDays, String outputDir,
                                            double[] N_0, double[] S_0, double[] I_0, double[] H_0, double[] R_0,
                                            double R0, double I_H_para, double I_R_para, double H_R_para) {

        logger.info("Start simulation for city: " + simulationCity);

        int gridLength = S_0.length;

        for (int fileIndex = 0; fileIndex < simulationDays * 6; fileIndex++) {
            // 加载分片的 OD 文件
            double[][][] OD = loadODFromNpy(ROOT_FILE_PATH + "\\1\\" + simulationCity + "\\OD_" + fileIndex + ".npy");

            if (OD == null) {
                logger.warning("Failed to load OD data for file index: " + fileIndex);
                continue;
            }

            for (int hourIndex = 0; hourIndex < 4; hourIndex++) { // 每个 OD 文件包含 4 小时数据
                logger.info("------------- Hour " + (hourIndex + fileIndex * 4) + " start -------------");

                // 数据四舍五入处理
                N_0 = roundArray(N_0, 4);
                S_0 = roundArray(S_0, 4);
                I_0 = roundArray(I_0, 4);
                H_0 = roundArray(H_0, 4);
                R_0 = roundArray(R_0, 4);

                // 计算移动人口
                double[] movePeople = subtractArrays(N_0, H_0);

                double[][] S_temp = new double[gridLength][1];
                double[][] I_temp = new double[gridLength][1];
                double[][] R_temp = new double[gridLength][1];

                for (int i = 0; i < gridLength; i++) {
                    if (movePeople[i] != 0) {
                        I_temp[i][0] = I_0[i] / movePeople[i];
                        S_temp[i][0] = S_0[i] / movePeople[i];
                        R_temp[i][0] = R_0[i] / movePeople[i];
                    } else {
                        I_temp[i][0] = 0;
                        S_temp[i][0] = 0;
                        R_temp[i][0] = 0;
                    }
                }

                // 计算移动分布
                double[][] S_people = multiplyMatrix(S_temp, OD[hourIndex]);
                double[][] I_people = multiplyMatrix(I_temp, OD[hourIndex]);
                double[][] R_people = multiplyMatrix(R_temp, OD[hourIndex]);

                S_people = roundMatrix(S_people, 4);
                I_people = roundMatrix(I_people, 4);
                R_people = roundMatrix(R_people, 4);

                // 更新 S, I, R 值
                S_0 = updatePopulation(S_0, S_people, gridLength);
                I_0 = updatePopulation(I_0, I_people, gridLength);
                R_0 = updatePopulation(R_0, R_people, gridLength);

                // 调整负值
                S_0 = adjustNegativeValues(S_0);
                I_0 = adjustNegativeValues(I_0);
                R_0 = adjustNegativeValues(R_0);

                // 计算新增感染、住院、恢复
                double[] m_infected = calculateNewInfections(R0, S_people, I_people, OD[hourIndex]);
                double[] s_infected = calculateStaticInfections(R0, S_0, I_0, N_0);
                double[] newInfected = addArrays(m_infected, s_infected);

                double[] newHospital = scaleArray(I_0, I_H_para);
                double[] newIRecovered = scaleArray(I_0, I_R_para);
                double[] newHRecovered = scaleArray(H_0, H_R_para);

                // 更新最终状态
                S_0 = subtractArrays(S_0, newInfected);
                I_0 = addArrays(subtractArrays(addArrays(I_0, newInfected), newHospital), negateArray(newIRecovered));
                H_0 = addArrays(subtractArrays(H_0, newHRecovered), newHospital);
                R_0 = addArrays(R_0, addArrays(newIRecovered, newHRecovered));

                S_0 = adjustNegativeValues(S_0);
                I_0 = adjustNegativeValues(I_0);
                H_0 = adjustNegativeValues(H_0);
                R_0 = adjustNegativeValues(R_0);

                N_0 = addArrays(addArrays(S_0, I_0), addArrays(H_0, R_0));

                // 保存结果
                saveResults(outputDir, hourIndex + fileIndex * 4, gridLength, S_0, I_0, H_0, R_0, newInfected, N_0);

                logger.info("Simulation step " + (hourIndex + fileIndex * 4) + " completed.");
            }
        }
    }

    public static double[][][] loadODFromNpy(String filePath) {
        try {
            // 使用 ND4J 加载 .npy 文件
            INDArray array = Nd4j.createFromNpyFile(new File(filePath));

            // 获取数组的形状
            long[] shape = array.shape();
            if (shape.length != 3) {
                throw new IllegalArgumentException("Expected a 3D array, but got shape: " + shape.length);
            }

            // 转换为 double[][][]
            double[][][] odArray = new double[(int) shape[0]][(int) shape[1]][(int) shape[2]];
            for (int i = 0; i < shape[0]; i++) {
                for (int j = 0; j < shape[1]; j++) {
                    for (int k = 0; k < shape[2]; k++) {
                        odArray[i][j][k] = array.getDouble(i, j, k);
                    }
                }
            }
            return odArray;

        } catch (Exception e) {
            System.err.println("Error loading OD file: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Helper methods
    private double[] roundArray(double[] array, int decimals) {
        double scale = Math.pow(10, decimals);
        return Arrays.stream(array).map(x -> Math.round(x * scale) / scale).toArray();
    }

    private double[][] roundMatrix(double[][] matrix, int decimals) {
        double scale = Math.pow(10, decimals);
        double[][] rounded = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                rounded[i][j] = Math.round(matrix[i][j] * scale) / scale;
            }
        }
        return rounded;
    }

    private double[] subtractArrays(double[] a, double[] b) {
        return Arrays.stream(a).map(i -> i - b[(int) Arrays.asList(a).indexOf(i)]).toArray();
    }

    private double[][] multiplyMatrix(double[][] a, double[][] b) {
        INDArray aNd = Nd4j.create(a);
        INDArray bNd = Nd4j.create(b);
        INDArray result = aNd.mmul(bNd);
        return result.toDoubleMatrix();
    }

    private double[] updatePopulation(double[] base, double[][] movement, int gridLength) {
        double[] sumOut = new double[gridLength];
        double[] sumIn = new double[gridLength];
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridLength; j++) {
                sumOut[i] += movement[i][j];
                sumIn[j] += movement[i][j];
            }
        }
        return addArrays(subtractArrays(base, sumOut), sumIn);
    }

    private double[] addArrays(double[] a, double[] b) {
        return Arrays.stream(a).map(i -> i + b[(int) Arrays.asList(a).indexOf(i)]).toArray();
    }

    private double[] negateArray(double[] array) {
        return Arrays.stream(array).map(x -> -x).toArray();
    }

    private double[] scaleArray(double[] array, double scale) {
        return Arrays.stream(array).map(x -> x * scale).toArray();
    }

    private double[] adjustNegativeValues(double[] array) {
        double negativeSum = Arrays.stream(array).filter(x -> x < 0).sum() * -1;
        int negativeCount = (int) Arrays.stream(array).filter(x -> x <= 0).count();
        int totalLength = array.length;

        return Arrays.stream(array).map(x -> x <= 0 ? 0 : x - negativeSum / (totalLength - negativeCount)).toArray();
    }

    private double[] calculateNewInfections(double R0, double[][] S_people, double[][] I_people, double[][] OD) {
        int columns = S_people[0].length; // 假设所有数组的列数一致
        double[] result = new double[columns];

        for (int j = 0; j < columns; j++) {
            double sumS = 0.0;
            double sumI = 0.0;
            double sumOD = 0.0;

            // 遍历每一行，累加当前列的数据
            for (int i = 0; i < S_people.length; i++) {
                sumS += S_people[i][j];
                sumI += I_people[i][j];
                sumOD += OD[i][j];
            }

            // 根据公式计算结果
            result[j] = sumOD != 0 ? R0 * 0.01 * sumS * sumI / sumOD : 0;
        }
        return result;
    }


    private double[] calculateStaticInfections(double R0, double[] S_0, double[] I_0, double[] N_0) {
        double[] result = new double[S_0.length];
        for (int i = 0; i < S_0.length; i++) {
            result[i] = N_0[i] != 0 ? R0 * 0.01 * S_0[i] * I_0[i] / N_0[i] : 0;
        }
        return result;
    }

    public static void saveResults(String outputDir, int timeIndex, int gridLength,
                                   double[] S_0, double[] I_0, double[] H_0, double[] R_0,
                                   double[] newInfected, double[] N_0) {
        try {
            // 将数组堆叠成一个 INDArray
            INDArray result = Nd4j.vstack(
                    Nd4j.create(S_0, new int[]{1, S_0.length}),
                    Nd4j.create(I_0, new int[]{1, I_0.length}),
                    Nd4j.create(H_0, new int[]{1, H_0.length}),
                    Nd4j.create(R_0, new int[]{1, R_0.length}),
                    Nd4j.create(newInfected, new int[]{1, newInfected.length}),
                    Nd4j.create(N_0, new int[]{1, N_0.length})
            );

            // 创建输出文件路径
            File outputFile = new File(outputDir, "simulation_result_" + timeIndex + ".npy");

            // 保存为 .npy 文件
            Nd4j.saveBinary(result, outputFile);

            logger.info("Simulation results saved to: " + outputFile.getPath());
        } catch (IOException e) {
            logger.severe("Error saving simulation results: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

