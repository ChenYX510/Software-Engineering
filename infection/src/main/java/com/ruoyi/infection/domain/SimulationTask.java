package com.ruoyi.infection.domain;
import java.util.List;

public class SimulationTask {
    private double R0;
    private double I_H_para;
    private double I_R_para;
    private double H_R_para;
    private String I_input;
    private String regionList;
    private int simulationDays;
    private String simulationCity;

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
}