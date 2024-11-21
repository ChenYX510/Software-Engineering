package com.ruoyi.infection.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
public class LockSimulationTimeRecord {
    private Integer id;
    private String lockRegionStartTime;
    private String lockRegionEndTime;
    private String simulationEndTime;
    private String city;
    private String state;

    // Constructors
    public LockSimulationTimeRecord() {}

    public LockSimulationTimeRecord(Integer id, String lockRegionStartTime, String lockRegionEndTime, String simulationEndTime, String city, String state) {
        this.id = id;
        this.lockRegionStartTime = lockRegionStartTime;
        this.lockRegionEndTime = lockRegionEndTime;
        this.simulationEndTime = simulationEndTime;
        this.city = city;
        this.state = state;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLockRegionStartTime() {
        return lockRegionStartTime;
    }

    public void setLockRegionStartTime(String lockRegionStartTime) {
        this.lockRegionStartTime = lockRegionStartTime;
    }

    public String getLockRegionEndTime() {
        return lockRegionEndTime;
    }

    public void setLockRegionEndTime(String lockRegionEndTime) {
        this.lockRegionEndTime = lockRegionEndTime;
    }

    public String getSimulationEndTime() {
        return simulationEndTime;
    }

    public void setSimulationEndTime(String simulationEndTime) {
        this.simulationEndTime = simulationEndTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}