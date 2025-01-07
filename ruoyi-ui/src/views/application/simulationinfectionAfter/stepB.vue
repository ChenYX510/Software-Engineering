<!-- 灾害模拟业务流程-步骤二 -->
<template>
  <div>
    <div class="title">传染病灾害模拟任务选择</div>
    <div class="layout-main">
      <el-row class="layout-row" :gutter="32" type="flex" justify="center">
        <!-- 参数设置面板 -->
        <el-col :span="9">
          <el-card>
            <div slot="header" style="font-weight: bold;">
              <i class="el-icon-setting"></i>
              灾害模拟参数设置
            </div>
            <el-form ref="form">
              <!-- 设置疫情数据 -->
              <div style="margin-bottom: 20px;">
                <el-form-item label="模拟方式:">
                  <span>传染病灾情发展模拟</span>
                </el-form-item>
                <!-- 设置传染病灾后模拟预测 -->
                <div v-show="infectionModel == 'after'">
                  <el-form-item label="设置感染后住院率:">
                    <el-popover placement="right" width="300" trigger="hover">
                      <p class="helpTitle">参数说明</p>
                      <p>单位时间内每个被感染的患者住院的概率(I_H_para)</p>
                      <i class="el-icon-question" slot="reference" style="margin-right: 5px;"></i>
                    </el-popover>
                    <!-- <el-input v-model="infectionParamAfter['I_H_para']" placeholder="请输入内容"></el-input> -->
                    <el-input-number v-model="infectionParamAfter['I_H_para']" :min="0" :max="1"
                                     :controls="false"></el-input-number>
                  </el-form-item>
                  <el-form-item label="设置感染后自愈率:">
                    <el-popover placement="right" width="300" trigger="hover">
                      <p class="helpTitle">参数说明</p>
                      <p>单位时间内每个被感染的患者自愈的概率(I_R_para)</p>
                      <i class="el-icon-question" slot="reference" style="margin-right: 5px;"></i>
                    </el-popover>
                    <!-- <el-input v-model="infectionParamAfter['I_R_para']" placeholder="请输入内容"></el-input> -->
                    <el-input-number v-model="infectionParamAfter['I_R_para']" :min="0" :max="1"
                                     :controls="false"></el-input-number>
                  </el-form-item>
                  <el-form-item label="设置住院后治愈率:">
                    <el-popover placement="right" width="300" trigger="hover">
                      <p class="helpTitle">参数说明</p>
                      <p>单位时间内每个住院的患者治愈的概率(H_R_para)</p>
                      <i class="el-icon-question" slot="reference" style="margin-right: 5px;"></i>
                    </el-popover>
                    <!-- <el-input v-model="infectionParamAfter['H_R_para']" placeholder="请输入内容"></el-input> -->
                    <el-input-number v-model="infectionParamAfter['H_R_para']" :min="0" :max="1"
                                     :controls="false"></el-input-number>
                  </el-form-item>
                  <el-form-item label="每日新增感染人数文件:">
                    <el-upload name="file" :auto-upload="false" :on-change="uploadSeirPopulationXlsx"
                               :file-list="infectionParamAfter['file']" action="">
                      <i class="el-icon-upload"></i>
                      <div class="el-upload__tip" slot="tip">
                        模拟时期每日新增感染人数的xlsx文件
                      </div>
                    </el-upload>
                  </el-form-item>
                </div>
              </div>

              <!-- 操作 -->
              <el-form-item>
                <el-button @click="submitTask" type="primary">提交任务</el-button>
              </el-form-item>


            </el-form>
          </el-card>
        </el-col>
        <!-- 模拟任务列表 -->
        <el-col :span="15">
          <el-card>
            <div slot="header" style="font-weight: bold;">
              <i class="el-icon-s-grid"></i>
              传染病灾害模拟任务
            </div>
            <el-table :data="infectionSimulationListCity" :key="tableKey" style="height: 50vh; overflow:auto;">
              <el-table-column label="序号" prop="id" :show-overflow-tooltip="true" width="80" >
                <template slot-scope="scope">
                  <span>{{ scope.row.id }}</span>
                </template>
              </el-table-column>
              <el-table-column label="位置" prop="city" align="center" :show-overflow-tooltip="true" width="80">
                <template slot-scope="scope">
                  <span>{{ parseCity(scope.row.city) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="灾害类型" prop="simulationType" :show-overflow-tooltip="true" width="100">
                <template slot-scope="scope">
                  <span>{{ typeNames[scope.row.simulationType] }}</span>
                </template>
              </el-table-column>
              <!-- <el-table-column label="是否封锁区域" align="center" :show-overflow-tooltip="true" width="120">
              <template slot-scope="scope">
                <span>{{ scope.row.useLock ? "是" : "否" }}</span>
              </template>
            </el-table-column> -->
              <el-table-column label="管控措施类型" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ lockTypeNames[scope.row.lockType] }}</span>
                </template>
              </el-table-column>
              <!-- <el-table-column label="已完成的模拟时长" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ scope.row.num_result.toFixed(0) }}</span>
              </template>
            </el-table-column> -->
              <el-table-column label="模拟总时长" align="center" :show-overflow-tooltip="true" width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.paraJson.simulation_days * 24 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="基本繁殖数" align="center" :show-overflow-tooltip="true" width="100">
                <template slot-scope="scope">
                  <span>{{ scope.row.paraJson.R0 }}</span>
                </template>
              </el-table-column>
              <el-table-column label="状态" align="center" width="80" prop="simulationDate">
                <template slot-scope="scope">
                  <i class="el-icon-loading" v-if="scope.row.status === 'processing'"></i>
                  <i class="el-icon-success" v-if="scope.row.status === 'finish'" style="color: green"></i>
                  <i class="el-icon-error" v-if="scope.row.status === 'no'" style="color: crimson"></i>
                </template>
              </el-table-column>
              <el-table-column label="任务提交时间" align="center" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <span>{{ scope.row.simulationTime }}</span>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
                <template slot-scope="scope">
                  <el-radio v-model="taskIdSelected" :label="scope.row.id"
                            :disabled="scope.row.simulationState === 'no'" size="mini" border
                            @input="clickTask(scope.row)">选择</el-radio>
                  <el-button size="mini" type="text" icon="el-icon-delete" :disabled="scope.row.simulationState === 'no'" style="color: crimson;"
                             @click="removeTask(scope.row)">删除</el-button>
                  <!-- <el-button size="mini" type="text" icon="el-icon-set-up"
                    :disabled="scope.row.simulationState === 'no'" @click="clickTask(scope.row)"> -->
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <el-dialog title="传染病灾前模拟初始感染点设置" :visible.sync="showInfectionInput" width="80%" center>
      <div slot="title" style="font-size: 18px; font-weight: bold;">传染病灾前模拟初始感染点设置</div>
      <infectionInput ref="infectionInput" :area="area" @updateInitInfectionList="updateInitInfectionList" />
      <div slot="footer">
        <el-button @click="showInfectionInput = false">取 消</el-button>
        <el-button type="primary" @click="finishInitInfection">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="传染病灾前模拟封锁区域设置" :visible.sync="showInfectionLock" width="80%" center>
      <div slot="title" style="font-size: 18px; font-weight: bold;">传染病灾前模拟封锁区域设置</div>
      <infectionLock ref="infectionLock" :area="area" @updateLockInfectionList="updateLockInfectionList"
                     @updateInfectionLockDay="updateInfectionLockDay" />
      <div slot="footer">
        <el-button @click="showInfectionLock = false">取 消</el-button>
        <el-button type="primary" @click="finishLockInfection">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import axios from "axios";
import ajax from "@/views/simulator/ajax";
import mapData from '@/views/simulator/components/mapData';
import serverInfo from '@/views/simulator/serverInfo';

import infectionInput from "@/views/simulator/components/infection/infectionInput.vue";
import infectionLock from "@/views/simulator/components/infection/infectionLock.vue";
import {getToken,setToken} from "../../../utils/auth";
import store from "@/store"

export default {
  name: "stepB",
  props: {
    city: {
      type: String,
      default: "",
    },
    cityName: {
      type: String,
      default: "",
    },
  },
  components: {
    infectionInput, infectionLock
  },
  data() {
    return {
      taskIdSelected: -1, //选择的任务ID
      area: [],
      // 任务类型
      type: "infection",
      infectionModel: "after",
      userId: store.state.user.id,
      typeNames: {
        'flood': '暴雨洪涝',
        'infection': '传染病',
        'explode': '危化品爆炸',
      },
      taskTypeNames: {
        'false': '模拟',
        'true': '推演',
      },

      infectionParamGamma: {
        type: "infection",
        Population: "1000",
        "Initial Infection": "3",
        "Crowd Speed": "0.01",
        "Infectious Range": "0.01",
      },
      infectionParamBefore: {
        //"simulationCity": "chongqing",
        "I_H_para": 0.03,
        "I_R_para": 0.11,
        "H_R_para": 0.11,
        "simulation_days": 1,
        "R0": 15,
      },
      infectionParamMADDPG: {
        "R0": 1.6,
        "Gamma": 0.45,
        "Theta": 0.15,
        "Number": 10,
        "Region": "新华",
        "file": [],
        "populationFile": [],
        "odFile": [],
      },
      infectionParamAfter: {
        "city": "guangzhou",
        "I_H_para": 0.0136,
        "I_R_para": 0.1922,
        "H_R_para": 0.111,
        "file": [],
      },
      infectionParamConfig: 'config1',           //疫情模拟配置
      infectionConfigOptions: {                   //疫情模拟配置选项
        'config1': '麻疹',
        'config2': '白喉',
        'config3': '天花',
        'config4': '风疹',
        'config5': '流行性腮腺炎',
        'config6': '百日咳',
        'config7': 'SARS',
        'config8': '流行性感冒',
        'config9': 'COVID-19',
        'config10': '中东呼吸综合征',
        'config11': '普通感冒',
        'config12': '水痘',
        'config13': '猴痘',
        'custom': '自定义'
      },
      initInfectionList: [],                      // 初始感染点
      lockInfectionList: [],                      // 封锁区域
      showInfectionInput: false,                  // 设置初始感染点对话框
      showInfectionLock: false,                   // 设置封锁区域对话框
      useLockInfection: false,                    // 是否使用封闭区域传染病模拟
      infectionLockDay: 1,                        // 区域封锁时长
      sumbitBothTask: false,                       // 是否同时提交不封锁和封锁区域任务
      lockTypeNames:                               // 封锁区域类型名称
        {
          0: '无管控措施',
          1: '人为管控措施',
          2: '强化学习动态管控措施'
        },
      infectionParamPerformance: {                  // 传染病模拟效率
        "city": "guangzhou",
        "R0": 2,
        "Gamma": 0.45,
        "Theta": 0.15,
        "start_number": 10,
        "start_region": "新华",
      },
      city4LevelNameList: ["新华"],                       //城市乡镇街道级名称


      // 文件列表
      files: [],
      tableData: [], // 模拟任务列表
      infectionSimulationList: [],                // 传染病模拟任务列表
      infectionSimulationListCity: [],            // 该城市的模拟任务列表
      tableKey: 0,
    };
  },
  created() {
      this.$store.dispatch("GetInfo").then(() => {
        console.log("获取用户信息成功");
        console.log(this.userId);
      }).catch(() => { });
  },
  mounted() {
    this.area = ["", this.city];
    this.inquireCityInfectionSimulation(this.userId, 0);
    this.inquireCityInfectionSimulation(this.userId, 1);
    this.inquireCityInfectionSimulation(this.userId, 2);
  },
  methods: {
    // 根据拼音获取城市名
    parseCity(city) {
      if (city in mapData) {
        return mapData[city].cityName
      } else {
        return city
      }
    },
    // 改变传染病参数配置
    onInfectionConfigChange(option) {
      const R0s = {
        'config1': 15, // '麻疹'
        'config2': 6.5, // '白喉'
        'config3': 6, //'天花'
        'config4': 6, //'风疹'
        'config5': 11, //'流行性腮腺炎'
        'config6': 5.5, //'百日咳'
        'config7': 2.9, //'SARS'
        'config8': 2, //'流行性感冒'
        'config9': 3.8, //'COVID-19'
        'config10': 0.5, //'中东呼吸综合征'
        'config11': 2.5, //'普通感冒'
        'config12': 11, //'水痘'
        'config13': 1.26, //'猴痘'
        'custom': 1, //自定义
      };
      this.infectionParamBefore['R0'] = R0s[option];
    },
    // 获取传染病模拟任务列表
    inquireCityInfectionSimulation(userId, lockType = 0) {

      let headers = {
        "Content-Type": "application/x-www-form-urlencoded",
      };
      const service = axios.create({
        baseURL: serverInfo.baseURL_infection,
        timeout: 16000000,
      });
      let url;
      if (lockType === 0)
        url = '/inquire_city_simulation_result';
      else if (lockType === 1)
        url = '/inquire_city_simulation_lock_result';
      else if (lockType === 2)
        url = '/query_city_simulation_MADDPG_result';

      function convertTime(time) {
        const items = time.split('_');
        return `${items[0]} ${items[1]}:${items[2]}:${items[3]}`;
      }
      function parseTime(time) {
        const items1 = time.split('_');
        const items2 = items1[0].split('-');
        const items = [...items2, ...items1];
        items.forEach((v, index, arr) => {
          arr[index] = parseInt(v);
        });
        return items;
      }

      // 使用 URLSearchParams 来构造表单数据
      const formData = new URLSearchParams();
      formData.append('userId', userId); // 将 userId 作为表单数据添加


      service
        .post(url, formData,{ headers: headers })
        .then((res) => {
          if (res.data.msg === "success") {
            this.$message({
              message: "传染病模拟任务列表获取成功",
              type: "success",
            });
            res.data.simulation_task.forEach((task) => {
              if (task.simulationRecordNum > 0 && task.simulationRecord.length > 0) {   // 该城市存在传染病模拟任务
                task.simulationRecord.forEach((record) => {
                  record.city = task.city;
                  record.simulationType = "infection";
                  record.simulationTime_str = record.simulationTime;
                  record.simulationTime = convertTime(record.simulationTime);
                  record.lockType = lockType;
                  if (record.taskState === "True")
                    record.status = "finish";
                  else
                    record.status = "processing";
                  this.infectionSimulationList.push(record);
                });
              }

            });
            this.infectionSimulationList.sort((a, b) => {
              const timeItems1 = parseTime(a.simulationTime);
              const timeItems2 = parseTime(b.simulationTime);
              for (let i = 0; i < timeItems1.length; ++i) {
                if (timeItems1[i] !== timeItems2[i]) {
                  return timeItems1[i] - timeItems2[i];
                }
              }
              return a.lockType - b.lockType;
            });
            this.infectionSimulationList.forEach((record, index) => {
              record.id = index + 1;
            });
            this.getMyCityInfectionSimulation();
            this.$emit("onChangeTaskList",this.infectionSimulationListCity);
          }
          else {
            this.$message({
              message: "传染病模拟任务列表获取失败：" + res.data.msg,
              type: "warning",
            });
          }

        })
        .catch((err) => {
          console.log(err);
          this.$message({
            message: "传染病模拟任务列表获取失败",
            type: "error",
          });
        });
    },
    // 筛选该城市的模拟任务
    getMyCityInfectionSimulation(){
      var infectionSimulationListCity = [];
      this.infectionSimulationList.forEach((task)=>{
        if(task.city === this.city){
          infectionSimulationListCity.push(task);
        }
      });
      this.infectionSimulationListCity = infectionSimulationListCity;
      this.tableKey ++;

    },
    // 上传文件
    uploadSeirPopulationXlsx(file) {
      let fileName = file.name;
      let parts = fileName.split('.')
      let ext = parts[parts.length - 1];
      let extensions = ["xlsx"];
      if (!extensions.includes(ext)) {
        this.$message.error(`无法上传格式为${ext}的文件！`);
        this.infectionParamAfter["file"] = [];
        return;
      }
      else {
        this.infectionParamAfter["file"].push(file);
      }
    },
    // 获取初始感染点对话框子组件中的初始感染点列表
    updateInitInfectionList(data) {
      this.initInfectionList = data;
    },
    // 完成初始感染点设置
    finishInitInfection() {
      this.$refs["infectionInput"].finishInitInfection();
      this.showInfectionInput = false;
    },
    // 获取封锁区域对话框子组件中的封锁区域列表
    updateLockInfectionList(data) {
      this.lockInfectionList = data;
    },
    updateInfectionLockDay(day) {
      this.infectionLockDay = day;
    },
    // 完成封锁区域设置
    finishLockInfection() {
      this.$refs["infectionLock"].finishLockInfection();
      this.showInfectionLock = false;
    },
    submitInfectionBefore(formData, useLock) {

      const loading = this.$loading({
        lock: true,
        text: "仿真环境上传中",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });

      let headers = {
        "Authorization": `Bearer ${getToken()}` // 将 token 添加到 Authorization 头部
      };
      const service = axios.create({
        baseURL: serverInfo.baseURL_infection,
        timeout: 16000000,
      });

      let url = (useLock) ? 'lock_simulation' : 'grid_simulation';
      console.log(formData);
        service
        .post(url, formData, { headers: headers })
        .then((res) => {
          // 如果响应中有新的 token，更新 Vuex 中的 token
          setToken(getToken());
          console.log("test");
          console.log(res);
          console.log(res.data);  // 查看data内容
          loading.close();
          if (res.data.data.status === true) {
            console.log("true");
            this.$message({
              message: "传染病模拟任务上传成功",
              type: "success",
            });
          }
          else {
            console.log("false");


            this.$message({
              message: res.data.msg,
              type: "warning",
            });
          }
        })
        .catch((err) => {
          console.log(err);
          loading.close();
          this.$message({
            message: "传染病模拟任务上传失败",
            type: "error",
          });
        });

    },
    // 提交任务
    submitTask() {
      // 传染病灾前模拟
      if (this.infectionModel === "before") {

        if (Number(this.infectionParamBefore["I_H_para"]) < 0) {
          return this.$message.error("感染后住院率必须大于0");
        }
        if (Number(this.infectionParamBefore["I_R_para"]) < 0) {
          return this.$message.error("感染后自愈率必须大于0");
        }
        if (Number(this.infectionParamBefore["H_R_para"]) < 0) {
          return this.$message.error("住院后治愈率必须大于0");
        }
        if (Number(this.infectionParamBefore["R0"]) < 0) {
          return this.$message.error("R0必须大于0");
        }
        if (Number(this.infectionParamBefore["simulation_days"]) < 0) {
          return this.$message.error("模拟总天数必须大于0");
        }

        this.infectionParamBefore["simulationCity"] = this.area[1];

        let I_input = {}, region_list = {};
        this.initInfectionList.forEach((infection, index) => {
          I_input[index] = infection.population;
          region_list[index] = infection.position;
        });

        this.infectionParamBefore['I_input'] = JSON.stringify(I_input);
        this.infectionParamBefore['regionList'] = JSON.stringify(region_list);
        this.$store.dispatch("GetInfo").then(() => {
          console.log("获取用户信息成功");
          console.log(this.userId);
        }).catch(() => {});
        this.infectionParamBefore['userId'] = this.userId;


        let formData = new FormData();
        for (let key in this.infectionParamBefore) {
          formData.append(key, this.infectionParamBefore[key]);
        }
        // 将 FormData 转换为普通对象
        let formDataObject = {};
        formData.forEach((value, key) => {
          formDataObject[key] = value;
        });

// 将对象转换为 JSON 字符串
        let formDataString = JSON.stringify(formDataObject);
        // 添加操作时间
        const executionTime = new Date() - this.infectionStartTime;
        this.infectionStartTime = new Date();

        // 不封锁区域
        if (!this.useLockInfection) {
          this.submitInfectionBefore(formDataString, false);
        }
        else  // 封锁区域
        {
          // 同时提交不封锁区域任务
          if (this.sumbitBothTask) {
            this.submitInfectionBefore(formDataString, false);
          }
          let formData_lock = new FormData();
          let lock_area = {};
          this.lockInfectionList.forEach((infection, index) => {
            lock_area[index] = infection.position;
          });
          this.infectionParamBefore['lock_area'] = JSON.stringify(lock_area);
          this.infectionParamBefore['lock_day'] = this.infectionLockDay;

          for (let key in this.infectionParamBefore) {
            formData_lock.append(key, this.infectionParamBefore[key]);
          }
          this.submitInfectionBefore(formData_lock, true);
        }


      }
      // 传染病灾后模拟预测
      else if (this.infectionModel === "after") {

        if (Number(this.infectionParamAfter["I_H_para"]) < 0) {
          return this.$message.error("感染后住院率必须大于0");
        }
        if (Number(this.infectionParamAfter["I_R_para"]) < 0) {
          return this.$message.error("感染后自愈率必须大于0");
        }
        if (Number(this.infectionParamAfter["H_R_para"]) < 0) {
          return this.$message.error("住院后治愈率必须大于0");
        }
        this.infectionParamAfter["city"] = this.area[1];

        const loading = this.$loading({
          lock: true,
          text: "仿真环境上传中",
          spinner: "el-icon-loading",
          background: "rgba(0, 0, 0, 0.7)",
        });

        let headers = {
          "Content-Type": "multipart/form-data",
        };
        const service = axios.create({
          baseURL: serverInfo.baseURL_infection,
          timeout: 16000000
        });
        let formData = new FormData();
        for (let key in this.infectionParamAfter) {
          if (key === "file") continue;
          formData.append(key, this.infectionParamAfter[key])
        }
        formData.append("file", this.infectionParamAfter["file"][0].raw);

        service
          .post('/get_DSIHR', formData, { headers: headers })
          .then((res) => {
            loading.close();
            this.$message({
              message: "仿真环境上传成功",
              type: "success",
            });
            this.status = {
              data: res.data,
            };
            this.drawType = "infection";
            this.drawer = true;
          })
          .catch((err) => {
            console.log(err);
            loading.close();
            this.$message({
              message: "仿真环境上传失败",
              type: "error",
            });
          });


      }
      // 传染病模拟效率
      else if (this.infectionModel === "performance") {

        if (Number(this.infectionParamPerformance["Gamma"]) < 0) {
          return this.$message.error("感染后住院率必须大于0");
        }
        if (Number(this.infectionParamPerformance["Theta"]) < 0) {
          return this.$message.error("住院后治愈率必须大于0");
        }
        if (Number(this.infectionParamPerformance["R0"]) < 0) {
          return this.$message.error("R0必须大于0");
        }
        if (Number(this.infectionParamPerformance["start_number"]) < 0) {
          return this.$message.error("初始感染人数必须大于0");
        }

        const loading = this.$loading({
          lock: true,
          text: "传染病模拟效率计算任务提交中",
          spinner: "el-icon-loading",
          background: "rgba(0, 0, 0, 0.7)",
        });

        let headers = {
          "Content-Type": "multipart/form-data",
        };
        const service = axios.create({
          baseURL: serverInfo.baseURL_infection,
          timeout: 16000000
        });

        let formData = new FormData();
        this.infectionParamPerformance["city"] = this.area[1];
        for (let key in this.infectionParamPerformance) {
          formData.append(key, this.infectionParamPerformance[key]);
        }

        service
          .post('/efficiencyComparison', formData, { headers: headers })
          .then((res) => {
            loading.close();
            this.$message({
              message: "传染病模拟效率计算任务提交成功",
              type: "success",
            });
            console.log("/efficiencyComparison Res:", res.data);
          })
          .catch((err) => {
            console.log(err);
            loading.close();
            this.$message({
              message: "传染病模拟效率计算任务提交失败",
              type: "error",
            });
          });

      }

    },
    // 点击任务
    clickTask(task) {
      this.$emit("onChangeTask", task);
    },
    // 删除任务
    removeTask(task){
      this.$modal
        .confirm(`是否删除传染病灾害模拟任务${task.id}？`)
        .then(function() {
        })
        .then(() => {
        })
        .catch(() => {
        })
    },
  },

};
</script>

<style lang="scss" scoped>
// 标题
.title {
  text-align: center;
  font-size: 48px;
  font-family: Source Han Sans CN-Bold, Source Han Sans CN;
  font-weight: bold;
  color: #1f4e79;
}

// 页面主体布局
.layout-main {
  display: flex;
  flex-direction: row;
  justify-content: center;
  width: 100%;
  margin: 10px auto;
  padding: 20px 10px;

  border-radius: 10px;
  box-shadow: 1px 1px 4px 2px rgba(39, 56, 98, 0.2);

  .layout-row {
    width: 100%;
  }
}

.helpTitle {
  text-align: center;
  font-weight: bold;
  font-size: large;
  margin: 5px 0;
}

.el-icon-question:hover {
  cursor: pointer;
}

.line {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
}
</style>
