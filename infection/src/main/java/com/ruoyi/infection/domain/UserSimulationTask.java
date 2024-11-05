package com.ruoyi.infection.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

//获取该用户的模拟任务列表
public class UserSimulationTask {
    private Integer unlockResultId;
    private String filepath;
    private String state;
    private Long cityId;
    private String cityName;

    // Getter 和 Setter 方法
    public Integer getUnlockResultId() {
        return unlockResultId;
    }

    public void setUnlockResultId(Integer unlockResultId) {
        this.unlockResultId = unlockResultId;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    // Override toString() method
    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("unlockResultId", unlockResultId)
                .append("filepath", filepath)
                .append("state", state)
                .append("cityId", cityId)
                .append("cityName", cityName)
                .toString();
    }
}
