package com.ruoyi.infection.domain;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
public class CitySimulationResult {
    private String city;
    private int simulationRecordNum;
    private List<SimulationcityRecord> simulationcityRecord;

    // Getters and Setters
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getSimulationRecordNum() {
        return simulationRecordNum;
    }

    public void setSimulationRecordNum(int simulationRecordNum) {
        this.simulationRecordNum = simulationRecordNum;
    }

    public List<SimulationcityRecord> getSimulationRecord() {
        return simulationcityRecord;
    }

    public void setSimulationRecord(List<SimulationcityRecord> simulationRecord) {
        this.simulationcityRecord = simulationRecord;
    }
}
