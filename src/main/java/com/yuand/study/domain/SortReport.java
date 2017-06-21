package com.yuand.study.domain;

/**
 * Created by Administrator on 2017/6/21 0021.
 */
public class SortReport {
    private String key;

    private int bjNum;
    private double bjIncome;
    private double bjCost;

    private int kbNum;
    private double kbIncome;
    private double kbCost;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getBjNum() {
        return bjNum;
    }

    public void setBjNum(int bjNum) {
        this.bjNum = bjNum;
    }

    public double getBjIncome() {
        return bjIncome;
    }

    public void setBjIncome(double bjIncome) {
        this.bjIncome = bjIncome;
    }

    public double getBjCost() {
        return bjCost;
    }

    public void setBjCost(double bjCost) {
        this.bjCost = bjCost;
    }

    public int getKbNum() {
        return kbNum;
    }

    public void setKbNum(int kbNum) {
        this.kbNum = kbNum;
    }

    public double getKbIncome() {
        return kbIncome;
    }

    public void setKbIncome(double kbIncome) {
        this.kbIncome = kbIncome;
    }

    public double getKbCost() {
        return kbCost;
    }

    public void setKbCost(double kbCost) {
        this.kbCost = kbCost;
    }
}
