package com.ruoyi.infection.domain;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SimulationTask {
    private double R0;
    private double I_H_para;
    private double I_R_para;
    private double H_R_para;

    @JsonProperty("I_input")  // 用于将 JSON 中的 I_input 映射到 Java 类中的 I_input
    private String I_input;

    @JsonProperty("region_list")  // 用于将 JSON 中的 region_list 映射到 Java 类中的 regionList
    private String regionList;

    private int simulationDays;
    private String lock_area;
    private int lock_day;
    private String simulationCity;
    private long userId;

    // Getters and Setters
    public double getR0() {
        return R0;
    }

    public void setR0(double R0) {
        this.R0 = R0;
    }

    public double getI_H_para() {
        return I_H_para;
    }

    public void setI_H_para(double I_H_para) {
        this.I_H_para = I_H_para;
    }

    public double getI_R_para() {
        return I_R_para;
    }

    public void setI_R_para(double I_R_para) {
        this.I_R_para = I_R_para;
    }

    public double getH_R_para() {
        return H_R_para;
    }

    public void setH_R_para(double H_R_para) {
        this.H_R_para = H_R_para;
    }

    public String getI_input() {
        return I_input;
    }

    public void setI_input(String I_input) {
        this.I_input = I_input;
    }

    public String getRegionList() {
        return regionList;
    }

    public void setRegionList(String regionList) {
        this.regionList = regionList;
    }

    public String getLock_area() {
        return lock_area;
    }

    public void setLock_area(String lock_area) {
        this.lock_area = lock_area;
    }

    public int getLock_day() {
        return lock_day;
    }

    public void setLock_day(int lock_day) {
        this.lock_day = lock_day;
    }

    public int getSimulationDays() {
        return simulationDays;
    }

    public void setSimulationDays(int simulationDays) {
        this.simulationDays = simulationDays;
    }

    public String getSimulationCity() {
        return simulationCity;
    }

    public void setSimulationCity(String simulationCity) {
        this.simulationCity = simulationCity;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
