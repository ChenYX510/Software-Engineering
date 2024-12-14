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

    @JsonProperty("regionList")  // 用于将 JSON 中的 region_list 映射到 Java 类中的 regionList
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

    public void setR0(String R0) {
        this.R0 = Double.parseDouble(R0);
    }

    public double getI_H_para() {
        return I_H_para;
    }

    public void setI_H_para(String I_H_para) {
        this.I_H_para = Double.parseDouble(I_H_para);
    }

    public double getI_R_para() {
        return I_R_para;
    }

    public void setI_R_para(String I_R_para) {
        this.I_R_para = Double.parseDouble(I_R_para);
    }

    public double getH_R_para() {
        return H_R_para;
    }

    public void setH_R_para(String H_R_para) {
        this.H_R_para = Double.parseDouble(H_R_para);
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

    public void setLock_day(String lock_day) {
        this.lock_day = Integer.parseInt(lock_day);
    }

    public int getSimulationDays() {
        return simulationDays;
    }

    public void setSimulationDays(String simulationDays) {
        this.simulationDays = Integer.parseInt(simulationDays);
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

    public void setUserId(String userId) {
        this.userId = Long.parseLong(userId);
    }
}
