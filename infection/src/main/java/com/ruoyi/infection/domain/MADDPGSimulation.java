package com.ruoyi.infection.domain;

public class MADDPGSimulation {
    private String simulationCity;
    // 模拟文件的文件名称，即创建时间
    private  String simulationFileName;
    private long userId;

    public String getSimulationCity() {
        return simulationCity;
    }

    public void setSimulationCity(String I_input) {
        this.simulationCity = simulationCity;
    }

    public String getSimulationFileName() {
        return simulationFileName;
    }

    public void setSimulationFileName(String I_input) {
        this.simulationFileName = simulationFileName;
    }

    public long getUserId(){
        return userId;
    }

    public void setUserId(long userId){
        this.userId = userId;
    }
}
