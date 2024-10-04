<!-- 灾害模拟业务流程-步骤三 -->
<template>
  <div>
    <div class="title">模拟推演结果</div>
    <div class="layout-main">
      <infectionSimulation type="infection" :idSuffix="infectionModel" :infectionModel="infectionModel" :status="status"
                           :taskList="taskList" @onChangeRisk="onChangeRisk" v-if="loaded" />
      <div class="chart-container chart-container-infection" style="position:relative;">
        <div id="chartInfection" class="chart"></div>
        <div style="position:absolute; top:3px;right:3px">
          <el-popover placement="bottom" trigger="hover">
            <el-table :data="taskList" max-height="200" size="mini" style="width: 30vw;"
                      :header-cell-style="{ 'text-align': 'center' }" :cell-style="{ 'text-align': 'center' }">
              <el-table-column property="id" label="序号" width="50"></el-table-column>
              <el-table-column property="city" label="城市" width="60">
                <template slot-scope="scope">
                  <span>{{ cityName }}</span>
                </template>
              </el-table-column>
              <el-table-column label="任务提交时间">
                <template slot-scope="scope">
                  <span>{{ scope.row.simulationTime }}</span>
                </template>
              </el-table-column>
              <el-table-column property="city" label="管控措施类型" width="120">
                <template slot-scope="scope">
                  <span>{{ infectionLockTypeNames[scope.row.lockType] }}</span>
                </template>
              </el-table-column>
              <el-table-column property="status" label="状态" width="50">
                <template slot-scope="scope">
                  <i class="el-icon-loading" v-if="scope.row.status === 'processing'"></i>
                  <i class="el-icon-success" v-if="scope.row.status === 'finish'" style="color: green"></i>
                  <i class="el-icon-error" v-if="scope.row.status === 'failed'" style="color: crimson"></i>
                </template>
              </el-table-column>
              <el-table-column label="参数" width="50">
                <template slot-scope="scope">
                  <el-popover placement="right" width="500" trigger="hover">
                    <div>
                      <div class="line">
                        <p class="label">
                          管控措施类型：
                        </p>
                        <p>{{ infectionLockTypeNames[scope.row.lockType] }}</p>
                      </div>
                      <div class="line">
                        <p class="label">
                          模拟总时长：
                        </p>
                        <p>{{ scope.row.para_json.simulation_days * 24 }}</p>
                      </div>
                      <div class="line">
                        <p class="label">
                          感染者住院率：
                        </p>
                        <p>{{ scope.row.para_json.I_H_para }}</p>
                      </div>
                      <div class="line">
                        <p class="label">
                          感染者自愈率：
                        </p>
                        <p>{{ scope.row.para_json.I_R_para }}</p>
                      </div>
                      <div class="line">
                        <p class="label">
                          住院者治愈率：
                        </p>
                        <p>{{ scope.row.para_json.H_R_para }}</p>
                      </div>
                      <div class="line">
                        <p class="label">
                          初始感染点：
                        </p>
                        <p>{{ scope.row.para_json.region_list }}</p>
                      </div>
                      <div class="line">
                        <p class="label">
                          初始感染人数：
                        </p>
                        <p>{{ scope.row.para_json.I_input }}</p>
                      </div>
                      <div class="line" v-if="scope.row.lockType === 1">
                        <p class="label">
                          封锁区域：
                        </p>
                        <p>{{ scope.row.para_json.lock_area }}</p>
                      </div>
                      <div class="line" v-if="scope.row.lockType === 1">
                        <p class="label">
                          封锁时长：
                        </p>
                        <p>{{ scope.row.para_json.lock_day }}</p>
                      </div>
                    </div>
                    <i class="el-icon-view" slot="reference" style="margin-right: 5px;"></i>
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template slot-scope="scope">
                  <el-button size="mini" type="text" icon="el-icon-plus" :disabled="scope.row.status !== 'finish'"
                             @click="onAddInfectionChart(scope.row)">
                    添加
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-button icon="el-icon-arrow-down" slot="reference" size="mini" style="padding:3px 8px;" type="primary"
                       plain></el-button>
          </el-popover>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import infectionSimulation from "@/views/simulator/components/infection/infectionSimulation-ol.vue";
// 图表相关函数
import *  as chartUtils from '@/views/application/chartUtils'
// 后端请求相关函数
import *  as requestUtils from '@/views/application/requestUtils'

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
    task: {
      type: Object,
    },
    taskList: {
      type: Array,
    },
  },
  components: {
    infectionSimulation
  },
  data() {
    return {
      status: {},
      loaded: false,
      infectionSimulationList: [],
      infectionLockTypeNames:                     // 封锁区域类型名称
        {
          0: '无管控措施',
          1: '人为管控措施',
          2: '强化学习动态管控措施'
        },
      //传染病感染总人数对比曲线
      chartInfection: null,
      infectionPopulationData:              // 传染病感染总人数数据
        {
          xName: '模拟时刻(小时)',
          yName: '感染总人数(人)',
          xData: {},
          yData: [],
          useZoom: true
        },
      riskSource: null,
    };
  },
  created() { },
  mounted() {
    console.warn(this.task);
    this.loadTaskData(this.task);
    this.initChart();
  },
  methods: {
    loadTaskData(task) {
      const loading = this.$loading({
        lock: true,
        text: "获取模拟任务数据中",
        spinner: "el-icon-loading",
        background: "rgba(0, 0, 0, 0.7)",
      });
      this.infectionModel = "before";
      this.status = task;
      loading.close();
      this.loaded = true;

    },
    initChart() {
      // 构建感染总人数折线图
      this.chartInfection = chartUtils.buildChartLine(this.infectionPopulationData, 'chartInfection', 'customed')
      chartUtils.resizeChartBottom(this.chartInfection, 'chartInfection')
      this.chartInfection.showLoading()
      // 获取感染总人数数据并更新折线图
      this.onAddInfectionChart(this.task);
    },
    // 添加传染病模拟任务到对比曲线中
    onAddInfectionChart(task) {
      // 获取感染总人数数据并更新折线图
      let names = ['无管控措施', '人为管控措施', '强化学习动态管控措施']
      requestUtils.getInfectionTotalPopulation(this, task.lockType, task.simulation_time, false, (res) => {
        let num_times = Math.max(this.infectionPopulationData.yData.length, res.length)
        this.infectionPopulationData.yData = []
        for (let t = 1; t <= num_times; ++t) {
          this.infectionPopulationData.yData.push(t)
        }
        let cityName = this.cityName;
        let name = `任务${task.id}-${cityName}-${names[task.lockType]}`
        this.infectionPopulationData.xData[name] = res
        chartUtils.updateLineChart(this.chartInfection, this.infectionPopulationData)
        this.chartInfection.hideLoading()
      })
    },
    onChangeRisk(riskSource){
      this.riskSource = riskSource;
      this.$emit("onChangeRisk",riskSource);
    }
  }
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

  width: 80vw;
  margin: 10px auto;
  padding: 20px 10px;

  border-radius: 10px;
  box-shadow: 1px 1px 4px 2px rgba(39, 56, 98, 0.2);

  // 图表容器
  .chart-container {
    width: 100%;
    margin: auto;
    height: 35vh;
    border-radius: 10px;
    transition: 0.3s;
    box-shadow: 5px 4px 4px 1px rgba(39, 56, 98, 0.2);
  }

  .chart-container:hover {
    box-shadow: 5px 4px 4px 3px rgba(39, 56, 98, 0.5);
  }

  .chart-container-infection {
    height: 45vh;
  }

  // 图表
  .chart {
    height: 100%;
    transition: 0.3s;
  }

}
</style>
<style>
.aMap {
  height: 90vh !important;
}
</style>
