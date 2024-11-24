package com.ruoyi.infection.domain;
public class SimulationRequest {
    private String city;
    private Integer simulationDay;
    private Integer simulationHour;
    private Integer thresholdInfected;
    private String simulationFileName;

    // Getter and Setter for city
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // Getter and Setter for simulationDay
    public Integer getSimulationDay() {
        return simulationDay;
    }

    public void setSimulationDay(Integer simulationDay) {
        this.simulationDay = simulationDay;
    }

    // Getter and Setter for simulationHour
    public Integer getSimulationHour() {
        return simulationHour;
    }

    public void setSimulationHour(Integer simulationHour) {
        this.simulationHour = simulationHour;
    }

    // Getter and Setter for thresholdInfected
    public Integer getThresholdInfected() {
        return thresholdInfected;
    }

    public void setThresholdInfected(Integer thresholdInfected) {
        this.thresholdInfected = thresholdInfected;
    }

    // Getter and Setter for simulationFileName
    public String getSimulationFileName() {
        return simulationFileName;
    }

    public void setSimulationFileName(String simulationFileName) {
        this.simulationFileName = simulationFileName;
    }
}

