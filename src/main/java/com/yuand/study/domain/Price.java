package com.yuand.study.domain;

/**
 * Created by Administrator on 2017/6/20 0020.
 */
public class Price {
    //'类型（省际部分-标准、省际部分-快包、省内部分）', "起","始","航空公司","距离","内部处理结算","航空结算","一干陆运结算","二干陆运结算",
    private int type;

    private String srcAdress;

    private String destAdress;

    private String airline;

    private double distance;

    private double innerFree;

    private double airFree;

    private double oneFree;

    private double twoFree;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSrcAdress() {
        return srcAdress;
    }

    public void setSrcAdress(String srcAdress) {
        this.srcAdress = srcAdress;
    }

    public String getDestAdress() {
        return destAdress;
    }

    public void setDestAdress(String destAdress) {
        this.destAdress = destAdress;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getInnerFree() {
        return innerFree;
    }

    public void setInnerFree(double innerFree) {
        this.innerFree = innerFree;
    }

    public double getAirFree() {
        return airFree;
    }

    public void setAirFree(double airFree) {
        this.airFree = airFree;
    }

    public double getOneFree() {
        return oneFree;
    }

    public void setOneFree(double oneFree) {
        this.oneFree = oneFree;
    }

    public double getTwoFree() {
        return twoFree;
    }

    public void setTwoFree(double twoFree) {
        this.twoFree = twoFree;
    }
}
