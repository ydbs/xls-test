package com.yuand.study.domain;

/**
 * Created by Administrator on 2017/6/20 0020.
 */
public class BaseInfo {
    /**
     * 省份名称
     */
    private String proName;
    /**
     * 城市名称
     */
    private String cityName;
    /**
     * 分区
     */
    private String zone;
    /**
     * 省会
     */
    private String capital;
    /**
     * 省内运程(km)
     */
    private double innerVoyage;
    /**
     * 到省运程(km)
     */
    private double toVoyage;
    /**
     * 省外运程(km)
     */
    private double outVoyage;
    /**
     * 总运程(km)
     */
    private double totalVoyage;

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public double getInnerVoyage() {
        return innerVoyage;
    }

    public void setInnerVoyage(double innerVoyage) {
        this.innerVoyage = innerVoyage;
    }

    public double getToVoyage() {
        return toVoyage;
    }

    public void setToVoyage(double toVoyage) {
        this.toVoyage = toVoyage;
    }

    public double getOutVoyage() {
        return outVoyage;
    }

    public void setOutVoyage(double outVoyage) {
        this.outVoyage = outVoyage;
    }

    public double getTotalVoyage() {
        return totalVoyage;
    }

    public void setTotalVoyage(double totalVoyage) {
        this.totalVoyage = totalVoyage;
    }
}
