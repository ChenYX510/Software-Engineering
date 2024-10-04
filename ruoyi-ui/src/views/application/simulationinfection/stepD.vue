<!-- 联防联控业务流程-步骤四 -->
<template>
  <div>
    <div class="title">联防联控预案模版选择</div>
    <div class="layout-main">
      <div>
        <h1 style="text-align: center">{{cityName}}市</h1>
        <h3 style="text-align: center;color: #99a9bf">模板：《上海突发公告卫生事件专项应急预案》</h3>
      </div>
      <div>
        <el-checkbox-group v-model="selectedItems">
          <el-checkbox class="list-group-item" v-for="item in items" :key="item.value" :label="item.value" :checked="true" @change="handleSelectionChange($event,item.value)">
            <el-row :span="24" :offset="5">
              <div class="grid-content bg-purple-dark">
                {{item.label}}
              </div>
            </el-row>
          </el-checkbox>
        </el-checkbox-group>
      </div>
    </div>
  </div>
</template>

<script>
import mapData from '@/views/simulator/components/mapData';

export default {
  name: "stepD",
  props: {
    city: {
      type: String,
      default: "",
    },
    cityName: {
      type: String,
      default: "",
    },
    task:"",
    taskId:"",
  },
  components: {
  },
  data() {
    return {
      selectedItems: [],
      scrollbarHeight: "",
      items:[
        {label:'1 总则',value:'1'},
        {label:'1.1 编制目的',value:'1-1'},
        {label:'1.2 编制依据',value:'1-2'},
        {label:'1.3 事件分级',value:'1-3'},
        {label:'1.4 适用范围',value:'1-4'},
        {label:'1.5 工作原则',value:'1-5'},
        {label:'2 组织体系',value:'2'},
        {label:'2.1 领导机构',value:'2-1'},
        {label:'2.2 应急联动机构',value:'2-2'},
        {label:'2.3 工作机构',value:'2-3'},
        {label:'2.4 专家机构',value:'2-4'},
        {label:'2.5 专业技术机构',value:'2-5'},
        {label:'3 预警预防与报告机制',value:'3'},
        {label:'3.1 信息监测',value:'3-1'},
        {label:'3.2 预警级别与发布',value:'3-2'},
        {label:'3.3 信息报告',value:'3-3'},
        {label:'4 应急响应',value:'4'},
        {label:'4.1 应急响应原则',value:'4-1'},
        {label:'4.2 分级响应',value:'4-2'},
        {label:'4.3 响应措施',value:'4-3'},
        {label:'4.4 应急响应终止',value:'4-4'},
        {label:'5 后期处置',value:'5'},
        {label:'5.1 后期评估',value:'5-1'},
        {label:'5.2 抚恤和补助',value:'5-2'},
        {label:'5.3 征用物资、劳务补偿',value:'5-3'},
        {label:'6 应急保障',value:'6'},
        {label:'6.1 技术保障',value:'6-1'},
        {label:'6.2 物资保障',value:'6-2'},
        {label:'6.3 经费保障',value:'6-3'},
        {label:'6.4 通信保障',value:'6-4'},
        {label:'6.5 交通保障',value:'6-5'},
        {label:'6.6 治安保障',value:'6-6'},
        {label:'7 监督管理',value:'7'},
        {label:'7.1 公众宣传教育',value:'7-1'},
        {label:'7.2 演练',value:'7-2'},
      ],
    };
  },
  created() { },
  mounted() {
    this.scrollbarHeight = document.documentElement.clientHeight * 0.7 + "px";
  },
  watch: {
    selectedItems: {
      handler(newVal) {
        this.$emit("onChangeItems", this.items, newVal); //将新的值传递给父组件
      },
      deep: true
    }
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

    handleSelectionChange(event,label) {
      if (label.indexOf("-") === -1)//说明此时为一个大章节的总标题，需要将其他小标题也设置为点击
      {
        if (event === true)//说明选上了总标题
        {
          // 选中了总标题，查找对应的小标题并设置为选中
          const subItems = this.items.filter(item => item.value.startsWith(label + "-"));
          // console.log(subItems)
          for (const subItem of subItems) {
            // console.log(subItem.value)
            if(this.selectedItems.indexOf(subItem.value)===-1) {
              this.selectedItems.push(subItem.value);
            }
          }
        } else {
          // 取消选中了总标题，查找对应的小标题并设置为取消选中
          const subItems = this.items.filter(item => item.value.startsWith(label + "-"));
          for (const subItem of subItems) {
            const index = this.selectedItems.indexOf(subItem.value);
            if (index !== -1) {
              this.selectedItems.splice(index, 1);
            }
          }
        }
      }
    },

  },
};
</script>
<style lang="scss" scoped>
html, body {
  height: 100%;
}
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
  overflow: auto;
  width: 100%;
  height: 80vh;
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

.list-group-item{
  margin-left: 20%;
}
.el-row {
  margin-bottom: 20px;
  &:last-child {
    margin-bottom: 0;
  }
}
.el-col {
  border-radius: 4px;
}
.bg-purple-dark {
  //background: #99a9bf;
  width: 700px;
}
.bg-purple {
  background: #d3dce6;
}
.bg-purple-light {
  background: #e5e9f2;
}
.grid-content {
  border-radius: 4px;
  min-height: 36px;
}
.row-bg {
  padding: 10px 0;
  background-color: #f9fafc;
}
</style>
