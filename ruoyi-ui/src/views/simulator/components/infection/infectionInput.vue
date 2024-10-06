<template>

</template>

<script>
// import * as mapUtils from "@/views/simulator/components/mapUtils";
// import * as mapUtils2 from "@/views/application/mapUtils";
//
//
// export default ({
//   name: "infectionInput",
//   props: {
//     area: {
//       type: Array,
//     },
//   },
//   watch: {
//     area() {
//       this.onAreaChange();
//     },
//   },
//   data() {
//     return {
//       idSuffix: "infectionInput",
//       map: null,
//       zoomLevel: 10,
//       initInfectionList: [],              //初始感染点列表
//       initInfectionPopulation: 10,                // 当前输入的初始感染人数
//       infectionAdding: false,             //是否在添加初始感染点
//
//       initInfectionLayer: null,        // 初始感染点图层
//     }
//   },
//   mounted() {
//     this.onAreaChange();
//   },
//   methods: {
//     // 更新地图城市
//     onAreaChange() {
//       if (this.map) {
//         this.map.setTarget(null);
//         this.map = null;
//       }
//       mapUtils.initOL(this);
//       let city = "guangzhou";
//       if (this.area.length >= 2)
//         city = this.area[1];
//       mapUtils.initOLMap(this, city);
//
//       this.initInfectionList = [];
//       this.initInfectionLayer = null;
//       this.infectionAdding = false;
//       this.finishInitInfection();
//     },
//     // 删除初始感染点
//     removeInfection(infection) {
//       this.initInfectionLayer.source.removeFeature(infection.feature)
//       this.initInfectionList.forEach(function (item, index, arr) {
//         if (item.id == infection.id) {
//           arr.splice(index, 1)
//         }
//       })
//     },
//     // 添加初始感染点
//     onAddInfection() {
//       if (this.infectionAdding) {
//         if (this.initInfectionLayer)           //删除绘制图层
//         {
//           this.map.removeInteraction(this.initInfectionLayer.draw)
//         }
//       }
//       else {
//         if (!this.initInfectionLayer) {
//           mapUtils2.addInfectionAdding(this)
//         }//添加绘制图层和形状图层
//         else {
//           this.map.addInteraction(this.initInfectionLayer.draw)
//         }  //添加绘制图层
//       }
//       this.infectionAdding = !this.infectionAdding;
//     },
//     // 清空初始感染点
//     onClearInfection() {
//       if (this.initInfectionLayer)           //删除绘制图层
//       {
//         this.map.removeInteraction(this.initInfectionLayer.draw);
//         this.map.removeLayer(this.initInfectionLayer.vector);
//       }
//       this.initInfectionList = [];
//       this.initInfectionLayer = null;
//       this.infectionAdding = false;
//       this.finishInitInfection();
//     },
//     finishInitInfection() {
//       this.$emit("updateInitInfectionList", this.initInfectionList);
//     },
//   },
// });
</script>

<style lang="scss" scoped>

</style>
