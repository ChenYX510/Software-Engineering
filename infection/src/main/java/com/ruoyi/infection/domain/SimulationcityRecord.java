package com.ruoyi.infection.domain;

public class SimulationcityRecord {
    private String simulationTime;
    private String taskState;
    private String paraJson;

    // Getters and Setters
    public String getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(String simulationTime) {
        this.simulationTime = simulationTime;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getParaJson() {
        return paraJson;
    }

    public void setParaJson(String paraJson) {
        this.paraJson = paraJson;
    }
}
